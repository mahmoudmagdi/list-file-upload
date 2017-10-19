# List-File-Upload

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.loefflefarn/list-file-upload/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.loefflefarn/list-file-upload)
[![Build Status](https://travis-ci.org/loefflefarn/list-file-upload.svg?branch=master)](https://travis-ci.org/loefflefarn/list-file-upload)
[![Coverage Status](https://coveralls.io/repos/github/loefflefarn/list-file-upload/badge.svg?branch=master)](https://coveralls.io/github/loefflefarn/list-file-upload?branch=master)

This addon is used to process a CSV, Excel or text file. To execute a clean processing, the corresponding variable (= Excel cell) must be annotated in the corresponding 
domain class (Excel -> Java) with the fileUpload annotation. The FileUpload annotation must be passed to a FileUploadProcessor. This serves to carry out a test on the 
corresponding, e.g. Excel cell. Further in the code examples.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/directory#!addon/list-file-upload

## Building and running demo

````
git clone git@github.com:loefflefarn/list-file-upload.git
mvn clean install
cd list-file-upload-demo
mvn jetty:run
````
To see the demo, navigate to http://localhost:8080/

## Issue tracking

Issues for this add-on are tracked [here](https://github.com/loefflefarn/list-file-upload/issues). All bug reports and feature requests are welcome. 

## Used dependencies

For parsing CSV, Excel and text files - https://github.com/julian-eggers/texin

## Configuration

You must annotate variables that are to be set during the file upload with the annotation `@FileUpload()`.

Example:

```java
public class DemoObject {
    @FileUpload(header = "first-name", converter = FirstNameDemoObjectUploadProcessor.class)
    private String firstName;

    @FileUpload(header = "last-name", converter = LastNameDemoObjectUploadProcessor.class)
    private String lastName;

    @FileUpload(header = "email", converter = EmailDemoObjectUploadProcessor.class)
    private String email;
    
    [...]
}
```

File cell processor example:
```java
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
```
### Simple

In the Vaadin UI, you must initialize the `SimpleFileUploadProcessor` and specify what should happen in case of a fault-free or incorrect file upload. 
The `SimpleFileUploadProcessor` must be passed to the upload element (from Vaadin) as` succeededListener`. 

Example:

```java
SimpleFileUploadProcessor<DemoObject> uploadProcessor = new SimpleFileUploadProcessor<>(DemoObject.class,
    (event, items) -> {
        gridSimpleItems.addAll(items);
        demoSimpleGrid.getDataProvider().refreshAll();
    }, (event, errorItems) -> {
        gridSimpleErrorItems.addAll(errorItems);
        importErrorSimpleGrid.getDataProvider().refreshAll();
    });

Upload upload = new Upload("Choose file...", uploadProcessor);
upload.addSucceededListener(uploadProcessor);
rootLayout.addComponent(upload);
```

### Stream

In the Vaadin UI, you must initialize the `StreamFileUploadProcessor` and specify what should happen in case of a fault-free or incorrect file upload. 
The `StreamFileUploadProcessor` must be passed to the upload element (from Vaadin) as` succeededListener`. 

Example:

```java
StreamFileUploadProcessor<DemoObject> uploadProcessor = new StreamFileUploadProcessor<>(DemoObject.class,
    item -> {
        gridStreamItems.add(item);
        demoStreamGrid.getDataProvider().refreshAll();
    }, error -> {
        gridStreamErrorItems.add(error);
        importErrorStreamGrid.getDataProvider().refreshAll();
    }, event -> Notification.show("Upload completed!"));

Upload upload = new Upload("Choose file...", uploadProcessor);
upload.addSucceededListener(uploadProcessor);
rootLayout.addComponent(upload);
```
