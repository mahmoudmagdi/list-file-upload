package io.loefflefarn.list.fileupload.domain;

import io.loefflefarn.list.fileupload.processor.EmailDemoObjectUploadProcessor;
import io.loefflefarn.list.fileupload.processor.FirstNameDemoObjectUploadProcessor;
import io.loefflefarn.list.fileupload.processor.LastNameDemoObjectUploadProcessor;

public class DemoObject {
    @FileUpload(FirstNameDemoObjectUploadProcessor.class)
    private String firstName;

    @FileUpload(LastNameDemoObjectUploadProcessor.class)
    private String lastName;

    @FileUpload(EmailDemoObjectUploadProcessor.class)
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
