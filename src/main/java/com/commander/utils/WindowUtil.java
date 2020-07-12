package com.commander.utils;

import com.commander.controller.ParentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * {@code WindowHelper} is a utility class and key component for handling interaction between Spring and JavaFx.
 * The method WindowUtil.loadFxml(String url)" returns a FXMLLoader that is aware of Spring Controller Beans and will dynamically load Spring Controllers.
 *
 * This how you inject a Spring Application Controller class with FXML objects from mark-up, as we would in a classic JavaFx Application.
 *
 * @author HGDIV with big thanks to:
 * (https://blog.jetbrains.com/idea/2019/11/tutorial-reactive-spring-boot-a-javafx-line-chart/) by: Trisha Gee
 */
public class WindowUtil {

    private static ConfigurableApplicationContext ctx;


    private WindowUtil() {
    }

    /**
     * @param ctx set a static ConfigAppContext for WindowHelper.loadFxml()
     */
    public static void setContext(ConfigurableApplicationContext ctx) {
        WindowUtil.ctx = ctx;

    }

    /**
     *
     * @param root The root Node of the JavaFx Scene Graph to reload with new content from FXML
     * @param path The path location of the FXML file to be loaded
     * @param stage The Stage object to target for setScene()
     * @param parameters For passing messages about application state between Controllers
     * @param <T> Value parameter of k,v message (usually String)
     * @throws Exception
     */
    public static <T> void replaceFxmlOnWindow(Pane root, String path, Stage stage, HashMap<String, T> parameters)
            throws Exception {
        FXMLLoader loader = loadFxml(path);

        root.getChildren().clear();
        root.getChildren().add(loader.load());

        ParentController parentController = loader.getController();
        parentController.init(stage, parameters);
    }

    /**
     *
     * @param stage The Stage object to target for setScene()
     * @param fxmlPath the path to FXML file
     * @param title the scene title to render
     * @param parameters message k,v passed from calling controller
     * @param <T> Message, usually a String
     * @return Stage returns the instance of the set stage
     * @throws Exception
     */
    public static <T> Stage open(Stage stage, String fxmlPath, String title, HashMap<String, T> parameters)
            throws Exception {

        stage.setTitle(title);
        stage.setResizable(true);

        FXMLLoader loader = loadFxml(fxmlPath);

        try {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add("src/main/resources/style/main.css");

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParentController parentController = loader.getController();
        parentController.init(stage, parameters);

        return stage;
    }

    /**
     * loadFxml(String url) takes advantage of {@code setControllerFactory()} from JavaFx Api,
     * which has a Callback parameter that we can use to provide FXMLLoader with Spring
     * Controller Beans.
     *
     * The FXMLLoader is then returned and used to load the Scene.
     *
     * @param url A String containing path to the fxml file to load
     * @return FXMLLoader with ability to load Spring controllers
     */
    public static FXMLLoader loadFxml(String url) {

        try (InputStream fxmlStream = WindowUtil.class.getResourceAsStream(url)) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(WindowUtil.class.getResource(url));
            loader.setControllerFactory(clazz -> ctx.getBean(clazz));

            return loader;
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}