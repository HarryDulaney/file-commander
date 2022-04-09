package com.commander.controller;

import com.commander.controller.converters.Converter;
import com.commander.controller.converters.ConvertibleFactory;
import com.commander.model.Convertible;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.service.ConvertService;
import com.commander.service.FileService;
import com.commander.utils.Constants;
import com.commander.utils.DialogHelper;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * {@code DragDropController} handles counterview, the FXMLView embedded in
 * the mainview and responsible for interacting with the user to
 * perform the actual file conversions.
 *
 * @author Harry Dulaney
 */
@Component
@FxmlView("/fxml/converterview.fxml")
public class DragDropController {

    private final FxWeaver fxWeaver;
    final Logger log = LoggerFactory.getLogger(DragDropController.class);

    private FileService fileService;
    private ConvertService convertService;
    private User user;
    private final ObservableList<Label> observableList;
    private final ListView<Label> listView;

    StringProperty obsSrcDirectory = new SimpleStringProperty();
    StringProperty obsTrgtDirectory = new SimpleStringProperty();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label superDirectoryLabel;
    @FXML
    private Label outputDirPathLbl;
    @FXML
    private BorderPane rootPane2;
    @FXML
    private Button runConvertButton;

    private HostServices hostServices;

    protected DragDropController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
        observableList = FXCollections.observableArrayList();
        listView = new ListView<>(observableList);
    }


    /**
     * {@code handleSelectedItemConvert(ActionEvent e)}
     * Handles determining the conversion to run based on
     * 1. Extension of the file selected for conversion and
     * 2. The users' preferences for that file type.
     *
     * @param event ActionEvent invoked when user selects to run file conversion.
     */
    @FXML
    private void handleSelectedItemConvert(ActionEvent event) {
        if (listView.getSelectionModel().getSelectedItem() == null) {
            DialogHelper.showErrorAlert("Please select a list item to convert to your preferred file type");
            return;
        }
        String fileName = listView.getSelectionModel().getSelectedItem().getText();

        if (fileName != null) {
            try {
                String ext = FilenameUtils.getExtension(fileName);
                Convertible convertible = null;
                switch (ext) {
                    case DocType
                            .CSV_ID: {
                        convertible = ConvertibleFactory.createCsvToXlsx(fileName,
                                user.getDirectoryPath(), user.getWriteDirectoryPath());
                        break;
                    }
                    case DocType.XLSX_ID: {
                        convertible = ConvertibleFactory.createXlsxToCsv(fileName,
                                user.getDirectoryPath(), user.getWriteDirectoryPath());
                        break;
                    }
                    case DocType.DOCX_ID: {
                        if (user.getDocPreference().getDocOperation().equals(Constants.DOCX2PDF)) {
                            convertible = ConvertibleFactory.createDocxToPdf(fileName,
                                    user.getDirectoryPath(), user.getWriteDirectoryPath());
                        }
                        break;
                    }
                    case DocType.PDF_ID: {
                        if (user.getDocPreference().getDocOperation().equals(Constants.CLONE_PDF_TO_DOCX)) {
                            convertible = ConvertibleFactory.createClonePDFtoDOCX(fileName,
                                    user.getDirectoryPath(), user.getWriteDirectoryPath());
                        } else {
                            convertible = ConvertibleFactory.createPdfToDocx(fileName,
                                    user.getDirectoryPath(), user.getWriteDirectoryPath());
                        }
                        break;
                    }

                    case DocType.PNG_ID: {
                        convertible = ConvertibleFactory.createPngConvert(fileName, user.getDirectoryPath(), user.getWriteDirectoryPath(), user.getImgPreference());
                        break;
                    }
                    case DocType.BMP_ID:
                    case DocType.JPG_ID:
                    case DocType.GIF_ID:
                        convertible = ConvertibleFactory.createImageConvert(fileName,
                                user.getDirectoryPath(), user.getWriteDirectoryPath(), user.getImgPreference());
                        break;


                    default:
                        throw new IllegalStateException("Unexpected value: " + fileName);
                }
                try {
                    convertService.convert(convertible);
                } catch (IllegalStateException e) {
                    DialogHelper.showErrorAlert("It looks like you are attempted to convert a file that is not supported");
                    e.printStackTrace();

                } catch (Exception e) {
                    log.error("Exception at convertible creation method in controller.." + e.getMessage());
                    e.printStackTrace();

                }

            } catch (FileAlreadyExistsException e2) {
                DialogHelper.snackbarToast(e2.getMessage());

            }
        }

    }


    @FXML
    public void initialize() {

        if (Objects.nonNull(user)) {

            superDirectoryLabel.textProperty().bind(obsSrcDirectory);
            outputDirPathLbl.textProperty().bind(obsTrgtDirectory);

            setLabels();
            initListView();
            updateSrcFileAndBgColorPreference();
        }
    }

    protected void updateSrcFileAndBgColorPreference() {
        HashMap<String, Object> resourceBundle = new HashMap<>();
        resourceBundle.put("delete.policy", user.getSourceFilePolicy());
        resourceBundle.put("default.bg.color", user.getReplaceBgColor());

        Converter.setResources(resourceBundle);

    }

    /**
     * {@code initListView()} handles updating and populating the ListView with files
     * from the users source file directory. It also, configures the Drag and Drop
     * behaviors for rapid adding of new files to the directory
     */
    private void initListView() {
        listView.setMinSize(600, 450);
        listView.setItems(observableList);
        listView.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        listView.setOnDragDropped(event2 -> {
            List<File> files = event2.getDragboard().getFiles();
            log.info("Dropped: " + files.size() + " files.");
            for (File file : files) {
                Path dirPath = Paths.get(user.getDirectoryPath());
                Path filePath = dirPath.resolve(FilenameUtils.getName(file.getAbsolutePath()));
                try (OutputStream outStream = new FileOutputStream(filePath.toFile())) {
                    try {
                        Files.copy(file.toPath(), outStream);
                        observableList.add(new Label(file.getName()));

                    } catch (IOException e) {
                        e.printStackTrace();
                        DialogHelper.showErrorAlert("Application encountered an exception while copying the file," +
                                " it is likely that the file is read only.");
                    }
                } catch (IOException fnfe) {
                    fnfe.printStackTrace();
                    DialogHelper.showErrorAlert("Something went wrong copying the file");

                }

            }
            event2.consume();
        });
        listView.setOnMousePressed(event -> {
            if (runConvertButton.isDisabled()) {
                if (listView.getSelectionModel().getSelectedItem() != null)
                    runConvertButton.setDisable(false);
            }
        });
        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() < 0) {
                if (newValue.intValue() > 0) {
                    runConvertButton.setDisable(false);
                }
            } else {
                if (newValue.intValue() < 0) {
                    runConvertButton.setDisable(true);
                }
            }
        });
        loadFilesList();
        scrollPane.setContent(listView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }


    /**
     * Handles Refresh button logic. Reloads files to populate the Drag and Drop
     * ListView with new Labels that represent files that the user would want to convert
     * based on an update to the User's Preferences
     * TODO: this should be triggered automatically when the SAVE event is fired from {@code RootController.class}.
     */
    private void loadFilesList() {
        List<Label> fileList = new ArrayList<>();
        fileService.getFilterDirectoryFiles(user, e -> {
            File[] files = (File[]) e.getSource().getValue();
            if (files.length > 0) {
                for (File file : files) {
                    fileList.add(new Label(file.getName()));
                }
                observableList.setAll(fileList);
            }


        }, null);
    }

    /**
     * {@code setLabels()} Initializes the clickable Labels and event listeners for the
     * source directory and output directory.
     */
    void setLabels() {

        obsSrcDirectory.set(user.getDirectoryPath());
        obsTrgtDirectory.set(user.getWriteDirectoryPath());

        superDirectoryLabel.setOnMouseClicked(event -> {
            Path p = Paths.get(user.getDirectoryPath());
            hostServices.showDocument(p.toUri().toString());
        });
        outputDirPathLbl.setOnMouseClicked(ev -> {
            Path path = Paths.get(user.getWriteDirectoryPath());
            hostServices.showDocument(path.toUri().toString());
        });

    }

    /**
     * Loads/Reloads the contents of list view
     *
     * @param actionEvent refreshListButton pressed
     */
    @FXML
    protected void handleRefreshListButton(ActionEvent actionEvent) {
        loadFilesList();
        DialogHelper.snackbarToast("Refreshed");
    }


    /**************************************************************
     * Autowire Beans
     **************************************************************/

    @Autowired
    private void setFileService(FileService fileService) {
        this.fileService = fileService;
    }


    @Autowired
    private void setConvertService(ConvertService convertService) {
        this.convertService = convertService;
    }

    @Autowired
    private void setUser(User user) {
        this.user = user;
    }

    @Autowired
    private void setHostServices(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HostServices hostServices) {
        this.hostServices = hostServices;
    }

    protected void handleUpdatePreferences() {
        loadFilesList();
        setLabels();
    }

}