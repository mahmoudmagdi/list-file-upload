package io.loefflefarn.list.fileupload.simple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.ImportError;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.domain.exception.NoParserAppliedException;
import com.itelg.texin.in.parser.CellProcessor;
import com.itelg.texin.in.processor.SimpleImportProcessor;

import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;
import io.loefflefarn.list.fileupload.domain.FileUploadException;

public class SimpleFileParseProcessor<T> extends SimpleImportProcessor<T> {
    private final Class<? super T> type;

    private boolean isAnyCellProcessorApplied = false;

    public SimpleFileParseProcessor(final Class<? super T> type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void mapRow(Row row) {
        try {
            Class<? extends T> targetClass = (Class<? extends T>) Class.forName(type.getName());
            T item = targetClass.newInstance();

            for (Cell cell : row.getCells()) {
                mapCell(item, cell);
            }

            if (!isAnyCellProcessorApplied) {
                throw new FileParseException(new NoParserAppliedException("No file-upload-processor applied"));
            }

            addItem(item);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new FileUploadException(e);
        }
    }

    private void mapCell(T item, Cell cell) {
        for (CellProcessor<T> processor : getProcessors(item)) {
            if (processor.applies(cell)) {
                try {
                    processor.process(item, cell);
                } catch (ContentValidationException e) {
                    addImportError(new ImportError(cell, e.getMessage()));
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
            throw new FileUploadException(e);
        }
    }
}
