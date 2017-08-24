package io.loefflefarn.list;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.itelg.texin.domain.ImportError;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import io.loefflefarn.list.fileupload.domain.DemoObject;
import io.loefflefarn.list.fileupload.domain.FileParseException;
import io.loefflefarn.list.fileupload.domain.FileUploadException;
import io.loefflefarn.list.fileupload.simple.SimpleFileUploadProcessor;
import io.loefflefarn.list.fileupload.stream.StreamFileUploadProcessor;

@Theme("mytheme")
public class MyUI extends UI {
    private static final long serialVersionUID = 2507171183439273525L;

    private final TabSheet root = new TabSheet();

    private final VerticalLayout simpleUploadLayout = new VerticalLayout();

    private final transient List<DemoObject> gridSimpleItems = new ArrayList<>();

    private final Grid<DemoObject> demoSimpleGrid = new Grid<>(DemoObject.class);

    private final transient List<ImportError> gridSimpleErrorItems = new ArrayList<>();

    private final Grid<ImportError> importErrorSimpleGrid = new Grid<>(ImportError.class);

    private final VerticalLayout streamUploadLayout = new VerticalLayout();

    private final transient List<DemoObject> gridStreamItems = new ArrayList<>();

    private final Grid<DemoObject> demoStreamGrid = new Grid<>(DemoObject.class);

    private final transient List<ImportError> gridStreamErrorItems = new ArrayList<>();

    private final Grid<ImportError> importErrorStreamGrid = new Grid<>(ImportError.class);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        initSimpleUploadButton();
        initSimpleDemoGrid();
        initSimpleImportErrorGrid();

        initStreamUploadButton();
        initStreamDemoGrid();
        initStreamImportErrorGrid();

        root.addTab(simpleUploadLayout, "Simple Upload");
        root.addTab(streamUploadLayout, "Stream Upload");
        setContent(root);

        setErrorHandler(event -> {
            if (event.getThrowable() instanceof FileUploadException) {
                Notification.show("Uploading error: " + ((FileUploadException) event.getThrowable()).getMessage(),
                        Type.ERROR_MESSAGE);
            }

            if (event.getThrowable() instanceof FileParseException) {
                Notification.show("Parsing error: " + ((FileParseException) event.getThrowable()).getMessage(),
                        Type.ERROR_MESSAGE);
            }
        });
    }

    private void initSimpleUploadButton() {
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
        simpleUploadLayout.addComponent(upload);
    }

    private void initSimpleDemoGrid() {
        demoSimpleGrid.setSizeFull();
        demoSimpleGrid.setDataProvider(new ListDataProvider<>(gridSimpleItems));
        simpleUploadLayout.addComponent(demoSimpleGrid);
    }

    private void initSimpleImportErrorGrid() {
        importErrorSimpleGrid.setSizeFull();
        importErrorSimpleGrid.setDataProvider(new ListDataProvider<>(gridSimpleErrorItems));
        simpleUploadLayout.addComponent(importErrorSimpleGrid);
    }

    private void initStreamUploadButton() {
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
        streamUploadLayout.addComponent(upload);
    }

    private void initStreamDemoGrid() {
        demoStreamGrid.setSizeFull();
        demoStreamGrid.setDataProvider(new ListDataProvider<>(gridStreamItems));
        streamUploadLayout.addComponent(demoStreamGrid);
    }

    private void initStreamImportErrorGrid() {
        importErrorStreamGrid.setSizeFull();
        importErrorStreamGrid.setDataProvider(new ListDataProvider<>(gridStreamErrorItems));
        streamUploadLayout.addComponent(importErrorStreamGrid);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        private static final long serialVersionUID = -119174817040487669L;
    }
}
