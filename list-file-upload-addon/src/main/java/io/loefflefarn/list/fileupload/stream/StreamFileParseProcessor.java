package io.loefflefarn.list.fileupload.stream;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.ImportError;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.domain.exception.NoParserAppliedException;
import com.itelg.texin.in.parser.CellProcessor;
import com.itelg.texin.in.processor.StreamingImportProcessor;

import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;

public class StreamFileParseProcessor<T> extends StreamingImportProcessor {
    private final Class<? super T> type;

    private final StreamFileUploadSuccessListener<T> successListener;

    private final StreamFileUploadFailureListener failureListener;

    private boolean isAnyCellProcessorApplied = false;

    private boolean isErrorOccured = false;

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

            if (!isErrorOccured) {
                successListener.listen(item);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new FileParseException(e);
        }
    }

    private void mapCell(final T item, final Cell cell) {
        for (CellProcessor<T> processor : getProcessors(item)) {
            if (processor.applies(cell)) {
                try {
                    processor.process(item, cell);
                } catch (ContentValidationException e) {
                    failureListener.listen(new ImportError(cell, e.getMessage()));
                    isErrorOccured = true;
                }

                isAnyCellProcessorApplied = true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<CellProcessor<T>> getProcessors(T item) {
        try {
            List<CellProcessor<T>> processors = new ArrayList<>();
            for (Field field : item.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FileUpload.class)) {
                    FileUpload upload = field.getAnnotation(FileUpload.class);
                    processors.add((CellProcessor<T>) upload.value().newInstance());
                }
            }
            return processors;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FileParseException(e);
        }
    }
}
