package io.loefflefarn.list.fileupload.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import io.loefflefarn.list.fileupload.domain.FileCellProcessor;
import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;

import org.junit.Test;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;

public class StreamFileParsingProcessorTest {
    private StreamFileParseProcessor<DemoObject> processor;

    @Test
    public void testMapRow() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "name", "Your name"));

        processor = new StreamFileParseProcessor<>(DemoObject.class, item -> {
            assertEquals("Your name", item.getName());
            assertNull(item.getEmail());
        }, error -> fail());
        processor.process(row);
    }

    @Test
    public void testMapRowWithContentValidationException() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "name", "Your name"));
        row.addCell(new Cell(row, 0, "email", "email@email.com"));

        processor = new StreamFileParseProcessor<>(DemoObject.class, item -> fail(), error -> {
            assertEquals("email", error.getCell().getColumnHeader());
            assertEquals("email@email.com", error.getCell().getStringValue());
            assertEquals("E-Mail not supported!", error.getError());
        });
        processor.process(row);
    }

    @Test(expected = FileParseException.class)
    public void testMapRowWithNoProcessorApplied() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "other-name", "Your name"));
        processor = new StreamFileParseProcessor<>(DemoObject.class, item -> fail(), error -> fail());
        processor.process(row);
    }

    static class DemoObject {
        @FileUpload(header = "name", converter = NameDemoObjectFileUploadProcessor.class)
        private String name;

        @FileUpload(header = "email", converter = EmailDemoObjectFileUploadProcessor.class)
        private String email;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        String getEmail() {
            return email;
        }
    }

    static class NameDemoObjectFileUploadProcessor implements FileCellProcessor<DemoObject> {
        @Override
        public void process(DemoObject item, Cell cell) throws ContentValidationException {
            item.setName(cell.getStringValue());
        }
    }

    static class EmailDemoObjectFileUploadProcessor implements FileCellProcessor<DemoObject> {
        @Override
        public void process(DemoObject item, Cell cell) throws ContentValidationException {
            throw new ContentValidationException("E-Mail not supported!");
        }
    }
}
