package com.commander;

import com.commander.controller.RootController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PrimaryStageInitializer implements ApplicationListener<com.commander.StageReadyEvent> {

    private final FxWeaver fxWeaver;
    private final String defaultStyleSheet =
            getClass().getClassLoader().getResource("style" + File.separator +"light.css").toExternalForm();

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
        scene.getStylesheets().add(defaultStyleSheet);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}