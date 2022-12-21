package com.commander.controller;

import com.commander.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/settingsview.fxml")
public class SettingsController {

    boolean displayed;

    @FXML
    private VBox settingsPane;

    @FXML
    public void initialize() {

    }

    @FXML
    public void closeSettingsView(ActionEvent event) {

    }

    @FXML
    public void saveSettingsAndClose(ActionEvent event) {

    }

    private User user;


    @Autowired
    private void setUser(User user) {
        this.user = user;
    }
}
