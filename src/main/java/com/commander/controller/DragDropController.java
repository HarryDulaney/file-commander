package com.commander.controller;

import com.commander.model.Convertible;
import com.commander.service.FileService;
import com.commander.utils.ConvertUtils;
import com.commander.utils.ConvertibleFactory;
import com.commander.utils.DialogHelper;
import com.jfoenix.controls.JFXListView;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;


/**
 * {@code DragDropController} is Controller for the list view
 * scene for performing file conversions
 *
 * @author HGDIV
 */

@Controller
public class DragDropController extends ParentController {

    final Logger log = LoggerFactory.getLogger(DragDropController.class);

    private ConfigurableApplicationContext ctx;
    private FileService fileService;

    private ObservableList<Label> observableList = FXCollections.observableArrayList();
    private JFXListView<Label> listView;

    @FXML
    private AnchorPane embeddedPane;
    @FXML
    private Label superDirectoryLabel;
    @FXML
    private Label outputDirPathLbl;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button runConvertButton;
    @FXML
    private Button refreshListButton;
    @FXML
    private FlowPane wordDocDisplayPane;
    @FXML
    private FlowPane excelPrefDisplayPane;
    @FXML
    private FlowPane imgPrefDisplayPane;


    private HostServices hostServices;


    /**
     * {@code handleSelectedItemConvert(ActionEvent e)}
     * This method handles running a file conversion.
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
            String ext = FilenameUtils.getExtension(fileName);
            Convertible convertible = null;
            switch (ext) {
                case "csv":
                    convertible = ConvertibleFactory.createCsvToXlsx(fileName,
                            user.getDirectoryPath(), user.getWriteDirectoryPath());
                    break;

                case "xlsx":
                    convertible = ConvertibleFactory.createXlsxToCsv(fileName,
                            user.getDirectoryPath(), user.getWriteDirectoryPath());
                    break;

                case DOCX2PDF:
                    convertible = ConvertibleFactory.createDocxToPdf(fileName,
                            user.getDirectoryPath(), user.getWriteDirectoryPath());
                    break;

                case PDFtxt2DOCX:
                    convertible = ConvertibleFactory.createPdfToDocx(fileName,
                            user.getDirectoryPath(), user.getWriteDirectoryPath());
                    break;
                case DOCX2HTML:

                    break;
                case HTML2DOCX:
                    break;
                case "bmp":
                case "jpg":
                case "gif":
                case "png":
                    convertible = ConvertibleFactory.createImageConvert(fileName,
                            user.getDirectoryPath(), user.getWriteDirectoryPath(), user.getImgPreference());
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + fileName);
            }
            try {
                fileService.convert(convertible);
            } catch (IllegalStateException e) {
                DialogHelper.showErrorAlert("I'm sorry it looks like you are attempted to convert a file that is not supported");
                e.printStackTrace();

            } catch (Exception e) {
                log.error("Exception @ FileServiceImpl.convert()");
            }


        } else {

            DialogHelper.showErrorAlert("Please select an item from the list before running a 'single-file' conversion.");

        }

    }


    @Override
    public <T> void init(Stage stage, HashMap<String, T> parameters) {
        super.init(stage, parameters);


        fileService = (FileService) ctx.getBean("fileService");
        String policy = user.getSourceFilePolicy();
        ConvertUtils.setToastPane(rootPane);

        if (policy.equals(PROJECT_SOURCE_DELETE_KEY)) {
            ConvertUtils.setDeleteSourceAfterConverted(true);

        } else {
            ConvertUtils.setDeleteSourceAfterConverted(false);
        }
        setPreferencesViewer();
        runConvertButton.setDisable(true);
        setLabels();
        initListView();
    }

    private void setPreferencesViewer() {
        Label wordPref = new Label(user.getDocPreference().getDocOperation().toUpperCase());
        wordPref.setFont(Font.font("SansSerif", 17.0));
        wordPref.setAlignment(Pos.CENTER);
        Label excelPref = new Label(user.getExcelPreference().getExtension().toUpperCase());
        excelPref.setFont(Font.font("SansSerif", 17.0));
        excelPref.setAlignment(Pos.CENTER);
        Label imgPref = new Label(user.getImgPreference().getExtension().toUpperCase());
        imgPref.setFont(Font.font("SansSerif", 17.0));
        imgPref.setAlignment(Pos.CENTER);


        wordDocDisplayPane.getChildren().add(wordPref);
        excelPrefDisplayPane.getChildren().add(excelPref);
        imgPrefDisplayPane.getChildren().add(imgPref);

    }

    /**
     * Reloads the contents of list view
     *
     * @param actionEvent refreshListButton pressed
     */
    @FXML
    private void handleRefreshListButton(ActionEvent actionEvent) {
        observableList.clear();
        fileService.getFilterDirectoryFiles(user, e -> {
            File[] files = (File[]) e.getSource().getValue();
            for (File file : files) {
                observableList.add(new Label(file.getName()));
            }

            listView.setItems(observableList);
        }, null);
    }


    /**
     * {@code setLabels()} Initializes the clickable Labels and event listeners for the
     * source directory and output directory.
     */
    private void setLabels() {
        superDirectoryLabel.setText(user.getDirectoryPath());
        outputDirPathLbl.setText(user.getWriteDirectoryPath());

        superDirectoryLabel.setOnMouseClicked(event -> {
            Path p = Paths.get(user.getDirectoryPath());
            hostServices.showDocument(p.toUri().toString());
        });
        outputDirPathLbl.setOnMouseClicked(ev -> {
            Path path = Paths.get(user.getWriteDirectoryPath());
            hostServices.showDocument(path.toUri().toString());
        });

//        filterChangeCheckBox.setOnAction(e -> toggleFileFilter());
    }

    /**
     * Handles checkbox selected / un-selected behavior. When selected,
     * the ListView populates with files in the source directory that need
     * to be converted, under the current preferences settings.
     */
    private void toggleFileFilter() {
        observableList.clear();
        runConvertButton.setDisable(true);
//        if (filterChangeCheckBox.isSelected()) {
        fileService.getFilterDirectoryFiles(user, e -> {
            File[] files = (File[]) e.getSource().getValue();
            for (File file : files) {
                observableList.add(new Label(file.getName()));
            }

            listView.setItems(observableList);
        }, null);

//        } else {
//            fileService.getDirectoryFiles(user, e -> {
//                File[] files = (File[]) e.getSource().getValue();
//                for (File file : files) {
//                    observableList.add(new Label(file.getName()));
//                }
//                listView.setItems(observableList);
//
//            }, null);
//
//
//        }


    }

    /**
     * {@code initListView()} handles updating and populating the ListView with files
     * from the users source file directory. It also, configures the Drag and Drop
     * behaviors for rapid adding of new files to the directory
     */
    private void initListView() {
        ScrollPane scrollPane = new ScrollPane();
        listView = new JFXListView<>();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        listView.setPrefSize(610, 610);
        toggleFileFilter();

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
                        DialogHelper.showErrorAlert("Application encountered an exception while copying the file, it is likely that the file is read only.");
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


        scrollPane.setContent(listView);
        embeddedPane.getChildren().add(scrollPane);

    }


    /**************************************************************
     * Autowire
     **************************************************************/

    @Autowired
    private void setConfigurableApplicationContext(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Autowired
    private void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    @Override
    protected void onClose() {
        ctx.close();
        fileService.onClose();

    }

}