package io.loefflefarn.list.fileupload.stream;

public interface StreamFileUploadSuccessListener<T> {
    void listen(final T item);
}
