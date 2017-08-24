package io.loefflefarn.list.fileupload.simple;

import java.util.Set;

import com.vaadin.ui.Upload;

public interface SimpleFileUploadSuccessListener<T> {
    void listen(final Upload.SucceededEvent event, final Set<T> items);
}
