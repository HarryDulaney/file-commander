package com.commander.controller;

import com.commander.model.DocOperation;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.utils.Constants;
import com.commander.utils.DialogHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
@FxmlView("/fxml/settingsview.fxml")
public class SettingsController {

    private User user;
    private final FxControllerAndView<DragDropController, BorderPane> dragDropController;
    private final FxControllerAndView<RootController, VBox> rootController;

    private final boolean isWindowsOS = System.getProperty("os.name").contains("Windows");
    private ToggleGroup ssheetGroup;
    private ToggleGroup imgGroup;


    public SettingsController(User user,
                              @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") FxControllerAndView<DragDropController, BorderPane> dragDropController,
                              @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") FxControllerAndView<RootController, VBox> rootController) {
        this.user = user;
        this.dragDropController = dragDropController;
        this.rootController = rootController;
    }

    @FXML
    private ColorPicker bgColorPicker;

    @FXML
    private VBox settingsPane;
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
    private RadioButton xlsxRadioButton;

    @FXML
    private RadioButton bmpRadioButton;
    @FXML
    private RadioButton gifRadioButton;
    @FXML
    private RadioButton pngRadioButton;
    @FXML
    private RadioButton jpgRadioButton;

    @FXML
    public void initialize() {
        directoryPathTextField.setText(user.getDirectoryPath());
        outputPathTextField.setText(user.getWriteDirectoryPath());
        configRadioButtonGroups();
        configComboBoxes();
    }

    @FXML
    public void closeSettingsView(ActionEvent event) {

    }

    @FXML
    public void saveSettingsAndClose(ActionEvent event) {

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

    @FXML
    private void handleColorChanged(ActionEvent actionEvent) {
        Color colorChoice = bgColorPicker.getValue();
        user.setReplaceBgColor(colorChoice);
        updatePreferenceValues("Background color to use when conversion doesn't support transparency set to " + colorChoice.toString());
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
        updatePreferenceValues("Text document file type preference changed to " + user.getDocPreference().getDocOperation());

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


    private void updatePreferenceValues(String message) {
        rootController.getController().updatePreferenceValues(message);
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
}
