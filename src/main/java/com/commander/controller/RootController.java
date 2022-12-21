package com.commander.controller;

import com.commander.model.DocOperation;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.service.FileService;
import com.commander.utils.Constants;
import com.commander.utils.DialogHelper;
import com.sun.javafx.stage.StageHelper;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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
    final Logger logger = LoggerFactory.getLogger(RootController.class);

    private final FxControllerAndView<DragDropController, BorderPane> dragDropController;
    private final FxWeaver fxWeaver;

    private User user;

    private FileService fileService;
    private HostServices hostServices;
    @FXML
    private VBox rootPane;
    @FXML
    private AnchorPane infoPane;

    @FXML
    private Label projectUserLabel;
    @FXML
    private MenuItem darkThemeMenuItem;
    @FXML
    private MenuItem lightThemeMenuItem;

    private final String LIGHT_THEME_STYLE;
    private final String DARK_THEME_STYLE;


    protected RootController(FxWeaver fxWeaver,
                             @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") FxControllerAndView<DragDropController, BorderPane> dragDropController,
                             @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HostServices hostServices,
                             User user,
                             FileService fileService) throws IOException {
        this.dragDropController = dragDropController;
        this.fxWeaver = fxWeaver;
        this.hostServices = hostServices;
        this.user = user;
        this.fileService = fileService;
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
    }


    /**
     * File menu button pressed to open the Settings view
     *
     * @param event ActionEvent
     */
    @FXML
    private void handleSettingsPressed(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("Settings and Preferences");
        Scene scene = new Scene(fxWeaver.loadView(SettingsController.class));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * {@code updatePreferenceValues(String message)}
     *
     * @param message to display on updating preferences
     */
    protected void updatePreferenceValues(String message) {
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
        hostServices.showDocument(Constants.GITHUB_URL);
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


    AnchorPane getInfoPane() {
        return infoPane;
    }


}