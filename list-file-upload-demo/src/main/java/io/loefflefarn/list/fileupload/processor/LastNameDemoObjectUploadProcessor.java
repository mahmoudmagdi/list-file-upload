package io.loefflefarn.list.fileupload.processor;

import org.apache.commons.lang3.StringUtils;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.in.parser.CellProcessor;

import io.loefflefarn.list.fileupload.domain.DemoObject;

public class LastNameDemoObjectUploadProcessor implements CellProcessor<DemoObject> {
    @Override
    public boolean applies(Cell cell) {
        return "last-name".equalsIgnoreCase(cell.getColumnHeader());
    }

    @Override
    public void process(DemoObject data, Cell cell) throws ContentValidationException {
        String value = cell.getStringValue();

        if (StringUtils.isNotBlank(value)) {
            data.setLastName(value);
        } else {
            throw new ContentValidationException("Field Last-Name required");
        }
    }
}
