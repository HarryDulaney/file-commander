package com.commander.service;

import com.commander.model.Convertible;
import com.commander.model.DocType;
import com.commander.model.User;
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

import java.util.Arrays;


/**
 * {@code FileServiceImpl.class} Spring Service Bean for handling
 * interaction with the User's file system
 *
 * @author HGDIV
 */
@Service("fileService")
public class FileServiceImpl extends ParentService implements FileService {

    /**
     * The Logger.
     */
    final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private static final String DEFAULT_OUTPUT_DIR_NAME = "super-commander-tmp";


    private FileServiceImpl() {
        super();

    }

    /**
     *
     * @param user
     * @param onSuccess
     * @param beforeStart
     * @return
     */
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

    /**
     * {@code getFilterDirectoryFiles} returns a filtered array of files to populate the Main ObservableList in the
     * user interface. The users input directory is iterated and files are added to the array if:
     * 1.  Their file extension belongs to a file type that this application is capable of processing
     * 2.  Their file extension does not belong to the file type of the users current output preference, because such
     *     a file is already in the preferred format and does not need to be converted.
     *
     *
     * @param user The current User
     * @param onSuccess Service worker indicates successful completion of the service
     * @param beforeStart Service worker indicates it's about to start the service
     * @return an Array of filtered files
     */
    @Override
    public javafx.concurrent.Service<File[]> getFilterDirectoryFiles(User user,
                                                                     EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> beforeStart) {
        return createService(new Task<File[]>() {
            final String docTypeExt = user.getDocPreference().getSourceFileExt();
            final String ssTypeExt = user.getExcelPreference().getExtension();
            final String imgTypeExt = user.getImgPreference().getExtension();

            protected File[] call() {
                final File file = new File(user.getDirectoryPath());
                FilenameFilter filter = (dir, name) -> !name.endsWith(docTypeExt) && !name.endsWith(ssTypeExt) && !name.endsWith(imgTypeExt)
                        && (name.endsWith(DocType.JPG.getExtension()) || name.endsWith(DocType.GIF.getExtension())
                        || name.endsWith(DocType.BMP.getExtension()) || name.endsWith(DocType.PNG.getExtension()) || name.endsWith(DocType.PDF.getExtension())
                        || name.endsWith(DocType.DOCX.getExtension()) || name.endsWith(DocType.CSV.getExtension()) || name.endsWith(DocType.XLSX.getExtension()));

                return file.listFiles(filter);

            }
        }, onSuccess, beforeStart);

    }

    /**
     * This method creates directory folder nested in the User's source directory in the event
     * <p>
     * 1.) SuperCommander can't write to the output directory
     * 2.) Application needs a backup folder to write info for the User to easily access
     *
     * @param onSuccess   service worker reports state after successful thread execution
     * @param beforeStart service worker reports state before executing thread
     * @return String of write path
     */
    @Override
    public javafx.concurrent.Service<String> writeOutputDirectory(EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> beforeStart) {
        return createService(new Task<String>() {
            protected String call() throws IOException {
                final String tmpFile = System.getProperty("java.io.tmpdir");

                Path path = Paths.get(tmpFile);
                Path writePath = path.resolve(DEFAULT_OUTPUT_DIR_NAME);
                Path p = Files.createDirectory(writePath);

                logger.info("Created temp file at" + p.toString());

                return p.toString();
            }
        }, onSuccess, beforeStart);
    }


}


