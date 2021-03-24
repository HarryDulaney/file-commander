package com.commander.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * {@code DialogHelper.class} is a helper class filled with static methods to utilize for Alerts, Pop-ups, UI
 * Dialogs, and SnackBar Toast's. This makes it simple to convey information to the application user.
 *
 * @author Harry Dulaney
 */
public final class DialogHelper {

    private static Logger log = LoggerFactory.getLogger(DialogHelper.class);
    private static AnchorPane displayNoticePane;

    public static void defaultDialog(StackPane root, String title, String body, DialogAction action) {

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(title));
        content.setBody(new Text(body));

        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.CENTER);

        JFXButton okButton = new JFXButton("Ok");
        okButton.setOnAction(event -> {
            dialog.close();
            action.onAction();

        });
        content.setActions(okButton);

        dialog.show();
    }

    public static void inputDialog(StackPane root, String title, String body, DialogAction action) {

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(title));
        content.setBody(new Text(body));

        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.CENTER);

        JFXButton yesButton = new JFXButton("Yes");
        yesButton.setOnAction(event -> {
            dialog.close();
            action.onAction();
        });
        content.setActions(yesButton);
        root.setOnMouseClicked(event -> {
            dialog.close();
        });
        JFXButton noButton = new JFXButton("No");
        noButton.setOnAction(event -> {
            dialog.close();
        });
        content.setActions(noButton);

        dialog.show();
    }

    public static File showFilePrompt(String title, String fileExtension1, String fileExtention2) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".csv", "*".concat(fileExtension1)),
                new FileChooser.ExtensionFilter(".xlsx", "*".concat(fileExtention2)));
        return fileChooser.showOpenDialog(new Stage(StageStyle.UTILITY));

    }

    public static File showFilePrompt(String title, String fileExtension, Boolean initFileName) {

        FileChooser fchooser = new FileChooser();
        fchooser.setTitle(title);
        fchooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileExtension, "*".concat(fileExtension)));

        if (initFileName) {
            fchooser.setInitialFileName("New_Excel_Workbook");
        }

        return fchooser.showSaveDialog(new Stage(StageStyle.UTILITY));
    }

    public static File showFilePrompt(String title, String fileExtension) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter(fileExtension, "*".concat(fileExtension)));
        return fileChooser.showOpenDialog(new Stage(StageStyle.TRANSPARENT));

    }

    public static String showInputPrompt(String header, String content, String title) {

        TextInputDialog textDialog = new TextInputDialog();
        textDialog.setHeaderText(header);
        textDialog.setContentText(content);
        textDialog.setTitle(title);
        textDialog.showAndWait();

        return textDialog.getResult();
    }

    public static void showWarningAlert(String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setContentText(content);
        alert.show();

    }

    public static void showErrorAlert(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public static void showInfoAlert(String content, String header, String title, boolean wait) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.showAndWait();


    }

    public static void showInfoAlert(String content, boolean wait) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(content);
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }

    }

    public static void showExceptionAlert(String contentText, Exception e) {

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(contentText);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

    }

    private static void displaySnackBarToast(String message) {

        JFXSnackbar snackbar = new JFXSnackbar(displayNoticePane);
        VBox vbox = new VBox();
        Label label = new Label(message);
        label.setFont(Font.font("SansSerif", FontWeight.BOLD, 16.0));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.DARKSLATEGRAY);
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(15.0));
        label.setWrapText(true);
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,
                new CornerRadii(3.5),
                new Insets(3.5))));
        vbox.getChildren().add(label);
        final JFXSnackbar.SnackbarEvent snackbarEvent = new JFXSnackbar.SnackbarEvent(vbox, Duration.seconds(2.33),
                null);
        snackbar.enqueue(snackbarEvent);
    }

    public static void snackbarToast(Pane pane, String message) {
        if (Objects.isNull(displayNoticePane)) {
            if (pane instanceof AnchorPane) {
                displayNoticePane = (AnchorPane) pane;
            }
        }
        displaySnackBarToast(message);
    }

    public static void snackbarToast(String message) {
        if (Objects.nonNull(displayNoticePane)) {
            displaySnackBarToast(message);
        }
    }
}