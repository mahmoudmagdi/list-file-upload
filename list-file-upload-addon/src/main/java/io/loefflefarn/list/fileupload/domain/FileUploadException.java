package io.loefflefarn.list.fileupload.domain;

public class FileUploadException extends RuntimeException {
    private static final long serialVersionUID = -5576080672787127663L;

    public FileUploadException(Throwable cause) {
        super(cause);
    }
}
