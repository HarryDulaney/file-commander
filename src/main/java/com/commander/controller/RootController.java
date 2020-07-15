package com.commander.controller;

import com.commander.model.DocType;
import com.commander.model.ExcelType;
import com.commander.model.ImgType;
import com.commander.model.User;
import com.commander.service.FileService;
import com.commander.utils.DialogHelper;
import com.commander.utils.WindowUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * The {@code RootController} class contains methods for controlling User interactions with
 * the preferences setting screen of the application.
 * <p>
 * This class handles all UI events for the File Menu Toolbar(Drop Down Menu)
 *
 * @author HGDIV
 */
@Controller
public class RootController extends ParentController {

    final Logger logger = LoggerFactory.getLogger(RootController.class);
    public static final String MES_KEY = "MESSAGE";


    private FileService fileService;
    private ToggleGroup ssheetGroup;
    private ToggleGroup docGroup;
    private ToggleGroup imgGroup;

    private HostServices hostServices;
    private ConfigurableApplicationContext ctx;

    @FXML
    private RadioButton bmpRadioButton;
    @FXML
    private RadioButton gifRadioButton;
    @FXML
    private RadioButton pngRadioButton;
    @FXML
    private RadioButton jpgRadioButton;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label projectUserLabel;
    @FXML
    private RadioButton xlsxRadioButton;
    @FXML
    private RadioButton csvRadioButton;
    @FXML
    private RadioButton pdfRadioButton;
    @FXML
    private RadioButton docxRadioButton;
    @FXML
    private TextField directoryPathTextField;
    @FXML
    private TextField outputPathTextField;
    @FXML
    private Button saveButton;
    @FXML
    private MenuItem openConverterButton;
    @FXML
    private ComboBox<String> prefsComboBox;
    @FXML
    private MenuItem saveUserPreferences;
    @FXML
    private MenuItem openPreferencesButton;


    /**
     * @param stage      The primary stage
     * @param parameters Are message resources passed from caller
     */
    @Override
    public <T> void init(Stage stage, HashMap<String, T> parameters) {
        super.init(stage, parameters);

        if (parameters != null) {
            String message = (String) parameters.get("MESSAGE");
            if (message.equals(FRESH_START)) {
                loadPreferences();
                openPreferencesButton.setDisable(true);


                if (user.getNuUser()) {
                    handleNewProject();
                    openConverterButton.setDisable(true);
                    saveButton.setDisable(true);
                } else {
                    // Welcome message
                }

            }
            docxRadioButton.setDisable(true);
            setProjectLabels();
            configRadioButtonGroups();
            configComboBox();

        }

    }

    /**
     * Configures the Source File Policy combobox with values and sets resets from
     * user preferences
     */
    private void configComboBox() {
        List<String> list = Arrays
                .asList(PROJECT_SOURCE_DELETE_KEY, PROJECT_SOURCE_SAVE_KEY);
        prefsComboBox.getItems().setAll(list);
        prefsComboBox.getSelectionModel().select(user.getSourceFilePolicy());

    }

    /**
     * @param event User's source file policy changed on UI
     */
    @FXML
    private void handleChangedValue(ActionEvent event) {
        String pref = prefsComboBox.getSelectionModel().getSelectedItem();
        user.setSourceFilePolicy(pref);

    }

    /**
     * @param event User clicked Save Preferences MenuItem on drop-down menu
     */
    @FXML
    private void handleSaveUser(ActionEvent event) {
        try {
            setPreferences();
            DialogHelper.showInfoAlert("Your preferences have been saved.", true);
        } catch (Exception e) {
            e.printStackTrace();
            DialogHelper.showErrorAlert("Something went wrong trying to save your preferences");
        }

    }


    /**
     * {@code handleSaveButton()}
     *
     * @param event User pressed Save button
     */
    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            setPreferences();

            DialogHelper.showInfoAlert(
                    "Your preferences have been saved \nNow you can drag and drop files to your directory folder", true);
            handleOpenConverter(event);

        } catch (Exception e3) {
            DialogHelper.showErrorAlert("Something went wrong,\nWe're not able to save your project.");
            e3.printStackTrace();
        }

    }

    /**
     * Handles dropdown menu "Open Preferences Settings" option
     *
     * @param event menu item clicked
     */
    @FXML
    private void handleOpenPreferences(ActionEvent event) {
        HashMap<String, String> pbMap = null;

        pbMap = new HashMap<>();
        pbMap.put(MES_KEY, OPEN_PREFERENCES);

        try {
            WindowUtil.replaceFxmlOnWindow(rootPane, PREF_EMBED_FXML, stage, pbMap);
            openPreferencesButton.setDisable(true);
            saveUserPreferences.setDisable(false);
            openConverterButton.setDisable(false);

        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    @FXML
    private void handleOpenConverter(ActionEvent event) {
        HashMap<String, String> pbMap = new HashMap<>();
        pbMap.put(MES_KEY, OPEN_CONVERTER);
        try {
            WindowUtil.replaceFxmlOnWindow(rootPane, DRAG_DROP_FXML, stage, pbMap);
            openPreferencesButton.setDisable(false);
            saveUserPreferences.setDisable(true);
            openConverterButton.setDisable(true);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    protected void handleGitHubOpen(ActionEvent event) {
        final String gitHubUrl = "https://www.github.com/HarryDulaney/SuperCommanderV2";
        hostServices.showDocument(gitHubUrl);


    }

    @FXML
    private void handleAssignDirectory(ActionEvent event) {
        final String HOME_KEY = System.getProperty("user.home");

        Path path = Paths.get(HOME_KEY);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(
                "Please create a folder or choose a folder for your file source directory");
        directoryChooser.setInitialDirectory(path.toFile());
        File result = directoryChooser.showDialog(new Stage());
        if (result == null) {
            DialogHelper.showErrorAlert(
                    "Please assign an input directory for reading source files.");

        } else {

            String strPath = result.getAbsolutePath();
            user.setDirectoryPath(strPath);
            directoryPathTextField.setText(strPath);

        }

        if (saveButton.isDisable()) {
            saveButton.setDisable(false);
        }
        if (openConverterButton.isDisable()) {
            openConverterButton.setDisable(false);
        }
    }

    @FXML
    private void handleAssignOutputDirectory(ActionEvent actionEvent) {
        final String HOME_KEY = System.getProperty("user.home");

        Path path = Paths.get(HOME_KEY);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(
                "Please create a folder or choose a folder where SuperCommander can write your converted files.");
        directoryChooser.setInitialDirectory(path.toFile());
        File result = directoryChooser.showDialog(new Stage());
        if (result == null) {
            DialogHelper.showErrorAlert(
                    "Please assign an output directory to write new files into.");

        } else {
            String writePath = result.getAbsolutePath();
            user.setWriteDirectoryPath(writePath);
            outputPathTextField.setText(writePath);

        }
    }

    @FXML
    private void handleExitPressed(ActionEvent event) {
        fileService.onClose();
        ctx.close();

        Platform.exit();
    }

    /**
     * Configures GUI labels and textfield saved values.
     */
    private void setProjectLabels() {

        projectUserLabel.setText(User.getCurrentUserId());
        directoryPathTextField.setText(user.getDirectoryPath());
        outputPathTextField.setText(user.getWriteDirectoryPath());


    }

    /**
     * Triggers Welcome message Pop-Up if User.nuUser evaluates to true
     */
    private void handleNewProject() {
        DialogHelper.showInfoAlert(
                "This is a new user profile. It is setup with default file settings"
                        + "\nPlease fill choose a folder for your file directory and adjust the file" +
                        "preferences to your liking and hit 'Save' to continue.",
                "Welcome to SuperCommanderV2.0", "New Project Profile", true);

        projectUserLabel.setText(User.getCurrentUserId());
    }

    private void configRadioButtonGroups() {
        ssheetGroup = new ToggleGroup();
        docGroup = new ToggleGroup();
        imgGroup = new ToggleGroup();

        switch (user.getDocPreference()) {
            case PDF:
                pdfRadioButton.setSelected(true);
                break;
            case DOCX:
                docxRadioButton.setSelected(true);
                break;
            default:
                pdfRadioButton.setSelected(false);
                docxRadioButton.setSelected(false);
        }
        switch (user.getExcelPreference()) {
            case XLSX:
                xlsxRadioButton.setSelected(true);
                break;
            case CSV:
                csvRadioButton.setSelected(true);
                break;
            default:
                xlsxRadioButton.setSelected(false);
                csvRadioButton.setSelected(false);

        }
        switch (user.getImgPreference()) {
            case BMP:
                bmpRadioButton.setSelected(true);
                break;
            case JPG:
                jpgRadioButton.setSelected(true);
                break;
            case GIF:
                gifRadioButton.setSelected(true);
                break;
            case PNG:
                pngRadioButton.setSelected(true);
                break;
            default:
                pngRadioButton.setSelected(false);
                gifRadioButton.setSelected(false);
                jpgRadioButton.setSelected(false);
                bmpRadioButton.setSelected(false);

        }


        xlsxRadioButton.setToggleGroup(ssheetGroup);
        csvRadioButton.setToggleGroup(ssheetGroup);
        ssheetGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            RadioButton excelChoice = (RadioButton) ssheetGroup.getSelectedToggle();

            if (excelChoice.getText().equalsIgnoreCase(ExcelType.XLSX.getExtension())) {
                user.setExcelPreference(ExcelType.XLSX);
            } else
                user.setExcelPreference(ExcelType.CSV);
        });

        pdfRadioButton.setToggleGroup(docGroup);
        docxRadioButton.setToggleGroup(docGroup);
        docGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton docChoice = pdfRadioButton;
            if (docChoice.getText().equalsIgnoreCase(DocType.PDF.getExtension())) {
                user.setDocPreference(DocType.PDF);
            } else
                user.setDocPreference(DocType.DOCX);
        });
        jpgRadioButton.setToggleGroup(imgGroup);
        bmpRadioButton.setToggleGroup(imgGroup);
        pngRadioButton.setToggleGroup(imgGroup);
        gifRadioButton.setToggleGroup(imgGroup);

        imgGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            RadioButton imgChoice = (RadioButton) observable.getValue();

            if (imgChoice.getText().equalsIgnoreCase(ImgType.JPG.getExtension())) {
                user.setImgPreference(ImgType.JPG);
            } else if (imgChoice.getText().equalsIgnoreCase(ImgType.BMP.getExtension())) {
                user.setImgPreference(ImgType.BMP);

            } else if (imgChoice.getText().equalsIgnoreCase(ImgType.GIF.getExtension())) {
                user.setImgPreference(ImgType.GIF);
            } else {
                user.setImgPreference(ImgType.GIF);
            }

        });

    }


    /***************************************************************
     * Autowire
     ***************************************************************/

    @Autowired
    void setConfigurableApplicationContext(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }


    @Autowired
    private void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    private void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    protected void onClose() {
        fileService.onClose();
        ctx.close();
        Platform.exit();

    }

}