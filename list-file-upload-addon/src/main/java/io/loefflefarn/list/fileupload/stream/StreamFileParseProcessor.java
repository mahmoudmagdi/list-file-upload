package io.loefflefarn.list.fileupload.stream;

import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;
import io.loefflefarn.list.fileupload.domain.FileUploadException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.ImportError;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.domain.exception.NoParserAppliedException;
import com.itelg.texin.in.parser.CellProcessor;
import com.itelg.texin.in.processor.StreamingImportProcessor;

public class StreamFileParseProcessor<T> extends StreamingImportProcessor {
    private final Class<? super T> type;

    private final StreamFileUploadSuccessListener<T> successListener;

    private final StreamFileUploadFailureListener failureListener;

    private boolean isAnyCellProcessorApplied = false;

    private boolean isErrorOccurred = false;

    public StreamFileParseProcessor(final Class<? super T> type,
                                    final StreamFileUploadSuccessListener<T> successListener,
                                    final StreamFileUploadFailureListener failureListener) {
        this.type = type;
        this.successListener = successListener;
        this.failureListener = failureListener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(final Row row) {
        try {
            Class<? extends T> targetClass = (Class<? extends T>) Class.forName(type.getName());
            T item = targetClass.newInstance();

            for (Cell cell : row.getCells()) {
                mapCell(item, cell);
            }

            if (!isAnyCellProcessorApplied) {
                throw new FileParseException(new NoParserAppliedException("No file-upload-processor applied"));
            }

            if (!isErrorOccurred) {
                successListener.listen(item);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new FileParseException(e);
        }
    }

    private void mapCell(final T item, final Cell cell) {
        getProcessors(item).forEach((header, converter) -> {
            if (cell.getColumnHeader().equalsIgnoreCase(header)) {
                try {
                    converter.process(item, cell);
                } catch (ContentValidationException e) {
                    failureListener.listen(new ImportError(cell, e.getMessage()));
                    isErrorOccurred = true;
                }

                isAnyCellProcessorApplied = true;
            }
        });
    }


    @SuppressWarnings("unchecked")
    private Map<String, CellProcessor<T>> getProcessors(T item) {
        try {
            Map<String, CellProcessor<T>> processors = new HashMap<>();

            for (Field field : item.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(FileUpload.class)) {
                    FileUpload upload = field.getAnnotation(FileUpload.class);
                    processors.put(upload.header(), (CellProcessor<T>) upload.converter().newInstance());
                }
            }

            return processors;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new FileUploadException(e);
        }
    }
}
