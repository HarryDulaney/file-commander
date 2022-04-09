package com.commander.service;

import com.commander.model.Convertible;
import com.commander.model.DocType;
import com.commander.model.User;
import com.commander.utils.Constants;
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

    private FileServiceImpl() {
        super();

    }

    /**
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
     * a file is already in the preferred format and does not need to be converted.
     *
     * @param user        The current User
     * @param onSuccess   Service worker indicates successful completion of the service
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
                if (user.getDirectoryPath() == null) {
                    try {
                        String inPath = writeInputDirectory();
                        user.setDirectoryPath(inPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (user.getWriteDirectoryPath() == null) {
                    try {
                        String outPath = writeOutputDirectory();
                        user.setDirectoryPath(outPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FilenameFilter filter = (dir, name) -> !name.endsWith(docTypeExt) && !name.endsWith(ssTypeExt) && !name.endsWith(imgTypeExt)
                        && (name.endsWith(DocType.JPG.getExtension()) || name.endsWith(DocType.GIF.getExtension())
                        || name.endsWith(DocType.BMP.getExtension()) || name.endsWith(DocType.PNG.getExtension()) || name.endsWith(DocType.PDF.getExtension())
                        || name.endsWith(DocType.DOCX.getExtension()) || name.endsWith(DocType.CSV.getExtension()) || name.endsWith(DocType.XLSX.getExtension()));

                final File file = new File(user.getDirectoryPath());
                if (file.exists()) {
                    return file.listFiles(filter);
                }
                return new File[0];


            }
        }, onSuccess, beforeStart);

    }

    /**
     * This method creates a default directory for converted files in User's tmpdir path
     * <p>
     * 1) SuperCommander can't write to the output directory
     * 2) User has not set a preference for output directory
     *
     * @return String of write path
     */
    public String writeOutputDirectory() throws IOException {
        Path writePath = Paths.get(Constants.DEFAULT_OUTPUT_DIR);
        Path p = Files.createDirectory(writePath);
        logger.info("Created write directory at" + p.toString());

        return p.toString();
    }

    /**
     * This method creates a default directory for input files in the User's tmpdir path
     * <p>
     * 1) SuperCommander can't read to the input directory
     * 2) User has not set a preference for input directory
     *
     * @return String of read path
     */
    public String writeInputDirectory() throws IOException {
        Path readPath = Paths.get(Constants.DEFAULT_INPUT_DIR);
        Path p = Files.createDirectory(readPath);
        logger.info("Created write directory at" + p.toString());

        return p.toString();
    }
}


