package io.loefflefarn.list.fileupload.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.in.parser.CellProcessor;

import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;
import io.loefflefarn.list.fileupload.simple.SimpleFileParseProcessor;

public class SimpleFileParsingProcessorTest {
    private final SimpleFileParseProcessor<DemoObject> processor = new SimpleFileParseProcessor<>(DemoObject.class);

    @Test
    public void testMapRow() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "name", "Your name"));
        row.addCell(new Cell(row, 1, "email", "youremail@localhost"));
        processor.mapRow(row);

        assertEquals(1, processor.getItems().size());
        assertEquals("Your name", processor.getItems().iterator().next().getName());
        assertEquals("youremail@localhost", processor.getItems().iterator().next().getEmail());
    }

    @Test(expected = FileParseException.class)
    public void testMapRowWithNoProcessorApplied() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "other-name", "Your name"));
        processor.mapRow(row);
    }

    static class DemoObject {
        @FileUpload(NameDemoObjectFileUploadProcessor.class)
        private String name;

        @FileUpload(EmailDemoObjectFileUploadProcessor.class)
        private String email;

        private String notForUpload;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNotForUpload() {
            return notForUpload;
        }

        public void setNotForUpload(String notForUpload) {
            this.notForUpload = notForUpload;
        }
    }

    static class NameDemoObjectFileUploadProcessor implements CellProcessor<DemoObject> {
        @Override
        public boolean applies(Cell cell) {
            return "name".equalsIgnoreCase(cell.getColumnHeader());
        }

        @Override
        public void process(DemoObject item, Cell cell) throws ContentValidationException {
            item.setName(cell.getStringValue());
        }
    }

    static class EmailDemoObjectFileUploadProcessor implements CellProcessor<DemoObject> {
        @Override
        public boolean applies(Cell cell) {
            return "email".equalsIgnoreCase(cell.getColumnHeader());
        }

        @Override
        public void process(DemoObject item, Cell cell) throws ContentValidationException {
            item.setEmail(cell.getStringValue());
        }
    }
}
