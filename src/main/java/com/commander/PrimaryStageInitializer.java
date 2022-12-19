package com.commander;

import com.commander.controller.RootController;
import com.commander.model.User;
import com.commander.utils.Constants;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.prefs.Preferences;

@Component
public class PrimaryStageInitializer implements ApplicationListener<com.commander.StageReadyEvent> {

    private final FxWeaver fxWeaver;
    private final String LIGHT_STYLE_SHEET =
            getClass().getClassLoader().getResource("light.css").toExternalForm();
    private final String DARK_STYLE_SHEET =
            getClass().getClassLoader().getResource("dark.css").toExternalForm();

    @Value("${application.ui.title}")
    private String title;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(com.commander.StageReadyEvent event) {
        Stage stage = event.stage;
        stage.setTitle(title);

        Scene scene = new Scene(fxWeaver.loadView(RootController.class));
        Preferences preferences = User.getUserPreferences();
        String themePreference = preferences.get(Constants.GUI_COLOR_THEME, Constants.LIGHT_THEME_ID);
        if (themePreference.equals(Constants.DARK_THEME_ID)) {
            scene.getStylesheets().add(DARK_STYLE_SHEET);
        } else {
            scene.getStylesheets().add(LIGHT_STYLE_SHEET);
        }

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}