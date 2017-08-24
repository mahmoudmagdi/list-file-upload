package io.loefflefarn.list.fileupload.simple;

import io.loefflefarn.list.fileupload.AbstractFileUploadProcessor;

public class SimpleFileUploadProcessor<T> extends AbstractFileUploadProcessor {
    private static final long serialVersionUID = -2384373073422086118L;

    @SuppressWarnings("unchecked")
    public SimpleFileUploadProcessor(final Class<? super T> type,
            final SimpleFileUploadSuccessListener<T> successListener,
            final SimpleFileUploadFailureListener failureListener) {
        super(new SimpleFileParseProcessor<>(type));

        setSucceededHandler((succeededEvent, parser) -> {
            if (((SimpleFileParseProcessor<T>) parser).getImportErrors().isEmpty()) {
                successListener.listen(succeededEvent, ((SimpleFileParseProcessor<T>) parser).getItems());
            } else {
                failureListener.listen(succeededEvent, ((SimpleFileParseProcessor<T>) parser).getImportErrors());
            }
        });
    }
}
