package io.loefflefarn.list.fileupload.processor;

import org.apache.commons.lang3.StringUtils;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.exception.ContentValidationException;
import com.itelg.texin.in.parser.CellProcessor;

import io.loefflefarn.list.fileupload.domain.DemoObject;

public class FirstNameDemoObjectUploadProcessor implements CellProcessor<DemoObject> {
    @Override
    public boolean applies(Cell cell) {
        return "first-name".equalsIgnoreCase(cell.getColumnHeader());
    }

    @Override
    public void process(DemoObject data, Cell cell) throws ContentValidationException {
        String value = cell.getStringValue();

        if (StringUtils.isNotBlank(value)) {
            data.setFirstName(value);
        } else {
            throw new ContentValidationException("Field First-Name is required");
        }
    }
}
