package io.loefflefarn.list.fileupload.stream;

import io.loefflefarn.list.fileupload.AbstractFileUploadProcessor;

import com.vaadin.ui.Upload.SucceededEvent;

public class StreamFileUploadProcessor<T> extends AbstractFileUploadProcessor {
    private static final long serialVersionUID = 1343833284889432881L;

    public StreamFileUploadProcessor(final Class<? super T> type,
                                     final StreamFileUploadSuccessListener<T> successListener,
                                     final StreamFileUploadFailureListener failureListener, final StreamSucceededHandler succeededHandler) {
        super(new StreamFileParseProcessor<>(type, successListener, failureListener));

        setSucceededHandler((succededEvent, items) -> succeededHandler.handle(succededEvent));
    }

    @FunctionalInterface
    public interface StreamSucceededHandler {
        void handle(final SucceededEvent event);
    }
}
