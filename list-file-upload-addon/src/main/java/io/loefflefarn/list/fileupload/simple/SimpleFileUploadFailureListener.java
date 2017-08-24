package io.loefflefarn.list.fileupload.simple;

import java.util.Set;

import com.itelg.texin.domain.ImportError;
import com.vaadin.ui.Upload;

public interface SimpleFileUploadFailureListener {
    void listen(Upload.SucceededEvent event, Set<ImportError> items);
}
