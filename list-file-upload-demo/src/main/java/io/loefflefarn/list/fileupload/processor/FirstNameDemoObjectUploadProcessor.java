package io.loefflefarn.list.fileupload.processor;

import com.itelg.texin.domain.Cell;
import com.itelg.texin.domain.exception.ContentValidationException;
import io.loefflefarn.list.fileupload.domain.DemoObject;
import io.loefflefarn.list.fileupload.domain.FileCellProcessor;
import org.apache.commons.lang3.StringUtils;

public class FirstNameDemoObjectUploadProcessor implements FileCellProcessor<DemoObject> {
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
