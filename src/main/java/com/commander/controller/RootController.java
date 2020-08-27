package com.commander.controller;

import com.commander.model.DocOperation;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.service.FileService;
import com.commander.utils.DialogHelper;
import com.commander.utils.ValidationUtils;
import com.commander.utils.WindowUtil;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
    private ToggleGroup imgGroup;
    private JFXSnackbar snackbar;
    private final boolean isWindowsOS = System.getProperty("os.name").contains("Windows");

    private HostServices hostServices;
    private ConfigurableApplicationContext ctx;

    @FXML
    private ColorPicker bgColorPicker;
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
    private ComboBox<String> textDocPrefsComboBox;
    @FXML
    private MenuItem saveUserPreferences;
    @FXML
    private MenuItem openPreferencesButton;


    /**
     * @param stage      The primary stage
     * @param parameters message resources pass info about application lifecycle
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
                    DialogHelper.snackbarToast(rootPane, "Welcome Back to File Commander!");
                }
            }
            setProjectLabels();
            configRadioButtonGroups();
            configComboBoxes();

        }

    }

    /**
     * Configures the Source File Policy Combobox and the Text Docs
     * Combobox
     */
    private void configComboBoxes() {
        //Src File Policy ComboBox configure
        List<String> list = Arrays
                .asList(PROJECT_SOURCE_DELETE_KEY, PROJECT_SOURCE_SAVE_KEY);
        prefsComboBox.getItems().setAll(list);
        prefsComboBox.getSelectionModel().select(user.getSourceFilePolicy());

        // Text Doc preferences ComboBox configure based on os = Windows or os = other
        List<String> docOpsList;

        if (isWindowsOS) {
            docOpsList = Arrays.asList(DocOperation.DOCX_TO_PDF.getDocOperation(),
                    DocOperation.PDF_txt_TO_DOCX.getDocOperation(),
                    DocOperation.PDF_TO_DOCX.getDocOperation());
        } else {
            docOpsList = Arrays.asList(DocOperation.DOCX_TO_PDF.getDocOperation(),
                    DocOperation.PDF_txt_TO_DOCX.getDocOperation());
        }

        textDocPrefsComboBox.getItems().setAll(docOpsList);
        textDocPrefsComboBox.getSelectionModel().select(user.getDocPreference().getDocOperation());

        //Configure ColorPicker combobox
        bgColorPicker.setValue(user.getReplaceBgColor());


    }

    /**
     * @param event change event triggered on src file preference combobox
     */
    @FXML
    private void handleSrcFilePrefChanged(ActionEvent event) {
        String pref = prefsComboBox.getSelectionModel().getSelectedItem();
        user.setSourceFilePolicy(pref);

    }

    /**
     * @param actionEvent change event triggered on Word Type combobox
     */
    @FXML
    private void handleTextDocPrefChanged(ActionEvent actionEvent) {
        String docPref = textDocPrefsComboBox.getSelectionModel().getSelectedItem();

        if (docPref.equals(DocOperation.PDF_txt_TO_DOCX.getDocOperation())) {
            user.setDocPreference(DocOperation.PDF_txt_TO_DOCX);

        } else if (docPref.equals(DocOperation.PDF_TO_DOCX.getDocOperation())) {
            user.setDocPreference(DocOperation.PDF_TO_DOCX);

        } else {
            user.setDocPreference(DocOperation.DOCX_TO_PDF);

        }
    }

    /**
     * @param event User clicked Save Preferences MenuItem on drop-down menu
     */
    @FXML
    private void handleSaveUser(ActionEvent event) {
        try {
            setPreferences();
            DialogHelper.snackbarToast(rootPane, "Your preferences have been saved.");
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
            handleOpenConverter(event);
            DialogHelper.snackbarToast(rootPane, "Your preferences have been saved");
        } catch (Exception e3) {
            DialogHelper.showErrorAlert("Something went wrong,\nWe're not able to save your preferences.");
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
        HashMap<String, String> pbMap;

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

        if (ValidationUtils.validateUser(user)) {

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
        } else {
            DialogHelper.showErrorAlert("Please confirm you have selected a preference for all fields before moving on.");
        }
    }

    @FXML
    protected void handleGitHubOpen(ActionEvent event) {
        final String gitHubUrl = "https://www.github.com/HarryDulaney/file-commander";
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
        if (result == null && user.getDirectoryPath() == null) {
            DialogHelper.showErrorAlert(
                    "Please assign an input directory for reading source files.");

        } else if (result != null) {
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
        if (result == null && user.getWriteDirectoryPath() == null) {
            DialogHelper.showErrorAlert(
                    "Please assign an output directory to write new files into, before moving on.");

        } else if (result != null) {
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

    @FXML
    private void handleColorChanged(ActionEvent actionEvent) {
        Color colorChoice = bgColorPicker.getValue();
        user.setReplaceBgColor(colorChoice);
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
        imgGroup = new ToggleGroup();

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

            if (excelChoice.getText().equalsIgnoreCase(DocType.XLSX.getExtension())) {
                user.setExcelPreference(DocType.XLSX);
            } else if (excelChoice.getText().equalsIgnoreCase(DocType.CSV.getExtension())) {
                user.setExcelPreference(DocType.CSV);
            } else {
                throw new UnsupportedOperationException("Something unexpected happened while setting the Excel preference radio buttons");
            }
        });

        jpgRadioButton.setToggleGroup(imgGroup);
        bmpRadioButton.setToggleGroup(imgGroup);
        pngRadioButton.setToggleGroup(imgGroup);
        gifRadioButton.setToggleGroup(imgGroup);

        imgGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            RadioButton imgChoice = (RadioButton) imgGroup.getSelectedToggle();

            if (imgChoice.getText().equalsIgnoreCase(DocType.JPG.getExtension())) {
                user.setImgPreference(DocType.JPG);
            } else if (imgChoice.getText().equalsIgnoreCase(DocType.BMP.getExtension())) {
                user.setImgPreference(DocType.BMP);

            } else if (imgChoice.getText().equalsIgnoreCase(DocType.GIF.getExtension())) {
                user.setImgPreference(DocType.GIF);
            } else if (imgChoice.getText().equalsIgnoreCase(DocType.PNG.getExtension())) {
                user.setImgPreference(DocType.PNG);
            } else {
                throw new UnsupportedOperationException("We were unable to process your image preference selection.");
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
        setPreferences();
        fileService.onClose();
        ctx.close();
        Platform.exit();

    }


}