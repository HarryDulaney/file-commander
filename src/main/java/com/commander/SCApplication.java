package com.commander;

import com.commander.controller.RootController;
import com.commander.utils.DialogHelper;
import com.commander.utils.WindowUtil;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;


/**
 * @author HGDIV
 */
@SpringBootApplication
public class SCApplication extends Application {

    private ConfigurableApplicationContext ctx;
    @Value
    ("${application.ui.title}")
    private String title;


            public static void main(String[]args) {
        launch(args);

    }

    @Override
    public void init() {
        ctx = SpringApplication.run(SCApplication.class);
        WindowUtil.setContext(ctx);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put(RootController.MES_KEY, RootController.FRESH_START);
            WindowUtil.open(primaryStage, RootController.getRootFxml(), title, messageMap);

        } catch (Exception ex) {
            ex.printStackTrace();
            DialogHelper.showWarningAlert("Application failed to load properly, please try again or contact the developers at -> github.com/harrydulaney/super-commander");
        }
    }

    @Bean
    HostServices initHostServices() {
        return this.getHostServices();
    }


    @Bean
    ConfigurableApplicationContext initCtx() {
        return this.ctx;

    }

    @Override
    public void stop() {
        ctx.close();
        Platform.exit();
    }
}
