package io.loefflefarn.list.fileupload;

import com.itelg.texin.domain.exception.NoParserAppliedException;
import com.itelg.texin.domain.exception.ParsingFailedException;
import com.itelg.texin.in.processor.AbstractImportProcessor;
import com.vaadin.ui.Upload;
import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUploadException;

import java.io.*;

public abstract class AbstractFileUploadProcessor implements Upload.Receiver, Upload.SucceededListener {
    private static final long serialVersionUID = -2996951785928913217L;

    protected final transient AbstractImportProcessor parsingProcessor;

    private transient SucceededHandler succeededHandler;

    private File file;

    public AbstractFileUploadProcessor(final AbstractImportProcessor parsingProcessor) {
        this.parsingProcessor = parsingProcessor;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            file = File.createTempFile("temp/file_uploads/" + Long.toString(System.nanoTime()), filename);

            return new FileOutputStream(file);
        } catch (IOException e) {
            delete(file);

            throw new FileUploadException(e);
        }
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            parsingProcessor.parse(file.getName(), fileInputStream);
            succeededHandler.handle(succeededEvent, parsingProcessor);
            delete(file);
        } catch (IOException e) {
            throw new FileUploadException(e);
        } catch (ParsingFailedException | NoParserAppliedException e) {
            throw new FileParseException(e);
        }
    }

    private boolean delete(File file) {
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public void setSucceededHandler(SucceededHandler succeededHandler) {
        this.succeededHandler = succeededHandler;
    }

    @FunctionalInterface
    public interface SucceededHandler {
        void handle(Upload.SucceededEvent succeededEvent, AbstractImportProcessor parser);
    }
}
