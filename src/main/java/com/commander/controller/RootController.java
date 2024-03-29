package com.commander.controller;

import com.commander.model.DocOperation;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.service.FileService;
import com.commander.utils.Constants;
import com.commander.utils.DialogHelper;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


/**
 * The {@code RootController} class contains methods for controlling User interactions with
 * the preferences setting screen of the application.
 * <p>
 * This class handles all UI events for the File Menu Toolbar(Drop Down Menu)
 *
 * @author Harry Dulaney
 */
@Component
@FxmlView("/fxml/mainview.fxml")
public class RootController {

    private final FxControllerAndView<DragDropController, BorderPane> dragDropController;
    private final FxWeaver fxWeaver;

    private User user;
    final Logger logger = LoggerFactory.getLogger(RootController.class);

    private FileService fileService;
    private ToggleGroup ssheetGroup;
    private ToggleGroup imgGroup;
    private final boolean isWindowsOS = System.getProperty("os.name").contains("Windows");

    private HostServices hostServices;

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
    private VBox rootPane;
    @FXML
    private AnchorPane infoPane;

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
    private ComboBox<String> prefsComboBox;
    @FXML
    private ComboBox<String> textDocPrefsComboBox;
    @FXML
    private MenuItem darkThemeMenuItem;
    @FXML
    private MenuItem lightThemeMenuItem;

    private final String LIGHT_THEME_STYLE;
    private final String DARK_THEME_STYLE;


    protected RootController(FxWeaver fxWeaver,
                             @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") FxControllerAndView<DragDropController, BorderPane> dragDropController) throws IOException {
        this.dragDropController = dragDropController;
        this.fxWeaver = fxWeaver;
        LIGHT_THEME_STYLE = getClass().getClassLoader().getResource("light.css").toExternalForm();
        DARK_THEME_STYLE = getClass().getClassLoader().getResource("dark.css").toExternalForm();


    }


    @FXML
    public void initialize() {
        if (user.getNuUser()) {
            handleNewProject();
        } else {
            DialogHelper.snackbarToast(infoPane, "Welcome Back to File Commander!");
        }

        setProjectLabels();
        configRadioButtonGroups();
        configComboBoxes();
    }

    /**
     * Configures the Source File Policy Combobox and the Text Docs
     * Combobox
     */
    private void configComboBoxes() {
        //Src File Policy ComboBox configure
        List<String> list = Arrays
                .asList(Constants.PROJECT_SOURCE_DELETE_KEY, Constants.PROJECT_SOURCE_SAVE_KEY);
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
        updatePreferenceValues("Source file handling policy changed to " + user.getSourceFilePolicy() + " the file " +
                "after conversion.");
        dragDropController.getController().updateSrcFileAndBgColorPreference();
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
        updatePreferenceValues("Text document file type preference changed to " + user.getDocPreference().getDocOperation());

    }

    /**
     * {@code updatePreferenceValues(String message)}
     *
     * @param message to display on updating preferences
     */
    private void updatePreferenceValues(String message) {

        Boolean success = user.setPreferences();
        if (success) {
            dragDropController.getController().handleUpdatePreferences();
            DialogHelper.snackbarToast(infoPane, message);
        } else {
            DialogHelper.showErrorAlert("Something went wrong,\nWe're not able to save your preferences.");
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

            updatePreferenceValues("Source folder updated.");

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
                    "Please assign an output directory to write new files into, before running conversions.");

        } else if (result != null) {
            String writePath = result.getAbsolutePath();
            user.setWriteDirectoryPath(writePath);
            outputPathTextField.setText(writePath);

            updatePreferenceValues("Output folder updated.");

        }
    }

    @FXML
    private void handleExitPressed(ActionEvent event) {
        fileService.onClose();
        updatePreferenceValues("Saving user preferences before exiting...");
        Platform.exit();
    }

    /**
     * Configures GUI labels and text field saved values.
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
        updatePreferenceValues("Background color to use when conversion doesn't support transparency set to " + colorChoice.toString());
        dragDropController.getController().updateSrcFileAndBgColorPreference();
    }

    /*  Handle Theme Toggle */

    @FXML
    private void handleInitLightTheme(ActionEvent actionEvent) {
        rootPane.getScene().getStylesheets().clear();
        rootPane.getScene().getStylesheets().add(LIGHT_THEME_STYLE);
        lightThemeMenuItem.setDisable(true);
        darkThemeMenuItem.setDisable(false);
        user.setColorThemePreference(Constants.LIGHT_THEME_ID);
        DialogHelper.snackbarToast(rootPane, "Light Theme Activated");
    }

    @FXML
    private void handleInitDarkTheme(ActionEvent actionEvent) {
        rootPane.getScene().getStylesheets().clear();
        rootPane.getScene().getStylesheets().add(DARK_THEME_STYLE);
        darkThemeMenuItem.setDisable(true);
        lightThemeMenuItem.setDisable(false);
        user.setColorThemePreference(Constants.DARK_THEME_ID);
        DialogHelper.snackbarToast(rootPane, "Dark Theme Activated");

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
            if (oldValue != newValue) {
                updatePreferenceValues("Excel file type preference changed to " + user.getExcelPreference().getExtension());

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
            if (oldValue != newValue) {
                updatePreferenceValues("Image file type preference changed to " + user.getImgPreference().getExtension().toUpperCase());


            }

        });

    }

    AnchorPane getInfoPane() {
        return infoPane;
    }


    /***************************************************************
     * Autowire Beans
     ***************************************************************/
    @Autowired
    void setUser(User user) {
        this.user = user;
    }


    @Autowired
    private void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    private void setHostServices(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HostServices hostServices) {
        this.hostServices = hostServices;
    }


}