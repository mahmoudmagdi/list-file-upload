package io.loefflefarn.list.fileupload.simple;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.Row;
import com.itelg.texin.domain.exception.ContentValidationException;
import io.loefflefarn.list.fileupload.domain.FileCellProcessor;
import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUpload;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SimpleFileParsingProcessorTest {
    private final SimpleFileParseProcessor<DemoObject> processor = new SimpleFileParseProcessor<>(DemoObject.class);

    @Test
    public void testMapRow() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "name", "Your name"));
        processor.mapRow(row);

        assertEquals(1, processor.getItems().size());
        assertEquals("Your name", processor.getItems().iterator().next().getName());
        assertNull(processor.getItems().iterator().next().getEmail());
    }

    @Test(expected = FileParseException.class)
    public void testMapRowWithNoProcessorApplied() {
        Row row = new Row(0);
        row.addCell(new Cell(row, 0, "other-name", "Your name"));
        processor.mapRow(row);
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

        void setEmail(String email) {
            this.email = email;
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
            item.setEmail(cell.getStringValue());
        }
    }
}
