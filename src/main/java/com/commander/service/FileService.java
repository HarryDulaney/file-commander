package com.commander.service;

import java.io.File;

import com.commander.model.Convertible;
import com.commander.model.User;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public interface FileService {

    javafx.concurrent.Service<File[]> getDirectoryFiles(User user, EventHandler<WorkerStateEvent> onSuccess,
                                      EventHandler<WorkerStateEvent> beforeStart);

    javafx.concurrent.Service<File[]> getFilterDirectoryFiles(User user, EventHandler<WorkerStateEvent> onSuccess,
                                            EventHandler<WorkerStateEvent> beforeStart);

    javafx.concurrent.Service<String> writeOutputDirectory(EventHandler<WorkerStateEvent> onSuccess,
                                                           EventHandler<WorkerStateEvent> beforeStart);

    void convert(Convertible convertible) throws Exception;

    void onClose();
}
