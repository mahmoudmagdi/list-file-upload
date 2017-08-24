package io.loefflefarn.list.fileupload.stream;

import com.itelg.texin.domain.ImportError;

public interface StreamFileUploadFailureListener {
    void listen(ImportError item);
}
