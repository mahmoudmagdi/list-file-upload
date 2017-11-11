package io.loefflefarn.list.fileupload.processor;

import io.loefflefarn.list.fileupload.domain.DemoObject;
import io.loefflefarn.list.fileupload.domain.FileCellProcessor;

import org.apache.commons.lang3.StringUtils;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.exception.ContentValidationException;

public class EmailDemoObjectUploadProcessor implements FileCellProcessor<DemoObject> {
    @Override
    public void process(DemoObject data, Cell cell) throws ContentValidationException {
        String value = cell.getStringValue();

        if (StringUtils.isNotBlank(value)) {
            throw new ContentValidationException("E-Mail is not supported currently");
        }
    }
}
