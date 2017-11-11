package io.loefflefarn.list.fileupload.processor;

import io.loefflefarn.list.fileupload.domain.DemoObject;
import io.loefflefarn.list.fileupload.domain.FileCellProcessor;

import org.apache.commons.lang3.StringUtils;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.exception.ContentValidationException;

public class LastNameDemoObjectUploadProcessor implements FileCellProcessor<DemoObject> {
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
