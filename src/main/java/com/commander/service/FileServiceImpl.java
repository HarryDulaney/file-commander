package com.commander.service;

import com.commander.model.Convertible;
import com.commander.model.User;
import com.commander.utils.DialogHelper;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@code FileServiceImpl.class} Spring Service Bean for handling
 *  interaction with the User's file system
 *
 * @author HGDIV
 */
@Service("fileService")
public class FileServiceImpl extends ParentService implements FileService {

    final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private static final String DEFAULT_OUTPUT_DIR_NAME = "SC-app-files";



    private FileServiceImpl() {
        super();

    }


    @Override
    public void convert(Convertible convertible) {
        convertible.convert();
    }


    @Override
    public javafx.concurrent.Service<File[]> getDirectoryFiles(User user, EventHandler<WorkerStateEvent> onSuccess,
                                                               EventHandler<WorkerStateEvent> beforeStart) {
        return createService(new Task<File[]>() {
            final FileSystemResource path = new FileSystemResource(user.getDirectoryPath());

            protected File[] call() {
                final File file = path.getFile();
                return file.listFiles();

            }
        }, onSuccess, beforeStart);

    }

    @Override
    public javafx.concurrent.Service<File[]> getFilterDirectoryFiles(User user,
                                                                     EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> beforeStart) {
        return createService(new Task<File[]>() {
            final String docTypeExt = user.getDocPreference().getDocOperation();
            final String ssTypeExt = user.getExcelPreference().getExtension();
            final String imgTypeExt = user.getImgPreference().getExtension();

            protected File[] call() {
                final File file = new File(user.getDirectoryPath());
                FilenameFilter filter = (dir, name) -> !name.endsWith(docTypeExt) && !name.endsWith(ssTypeExt) && !name.endsWith(imgTypeExt);

                return file.listFiles(filter);

            }
        }, onSuccess, beforeStart);

    }

    /**
     * This method creates directory folder nested in the User's source directory in the event
     *
     * 1.) SuperCommander can't write to the output directory
     * 2.) Application needs a backup folder to write info for the User to easily access
     *
     * @param directoryPath User's input source directory folder
     * @param onSuccess service worker reports state after successful thread execution
     * @param beforeStart service worker reports state before executing thread
     * @return String of write path
     */
    @Override
    public javafx.concurrent.Service<String> writeOutputDirectory(String directoryPath,
                                                                  EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> beforeStart) {
        return createService(new Task<String>() {
            protected String call() throws IOException {
                Path path = Paths.get(directoryPath);
                Path writePath = path.resolve(DEFAULT_OUTPUT_DIR_NAME);
                File file = writePath.toFile();
                if (file.exists()) {
                    DialogHelper.showErrorAlert("Write directory already exists.");
                    return file.getAbsolutePath();
                } else {
                    Path p = Files.createDirectory(writePath);
                    return p.toString();
                }

            }

        }, onSuccess, beforeStart);
    }


}


