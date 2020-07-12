package com.commander;

import com.commander.model.DocType;
import com.commander.model.ExcelType;
import com.commander.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;


/**
 * @author HGDIV
 */

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SCTest extends SCApplication {

    Logger logger = LoggerFactory.getLogger(SCTest.class);

    @Value("${application.test.inputpath}")
    String fileInputTestPath;

    @Value("${application.test.outputpath}")
    String fileOutputTestPath;

    @Autowired
    User user;

    User testUser;

    final static String success = "Success";
    final static String failed = "Failed";

    boolean SystemLevelAccessTest = true;
    boolean UserPersistTest = false;
    boolean PreferencesTest = false;
    boolean RetrieveVals = false;
}


//    @Test
//    void contextLoads() {
//        if (SystemLevelAccessTest) {
//            System.out.println("SYSTEM LEVEL ACCESS TEST: " + checkSystemLevelAccess());
//        }
//        if (PreferencesTest) {
//            //TODO: Preferences Test
//        }
//        if (UserPersistTest) {
//            user = new User();
//            user.setUserName("userToTest");
//            User.setDocPreference(DocType.DOCX);
//            User.setExcelPreference(ExcelType.CSV);
//            user.setSourceFilePolicy(TestResource.PROJECT_SOURCE_SAVE_KEY);
//            user.setDirectoryPath(System.getProperty("java.io.tmpdir"));
//            user.setWriteDirectoryPath(System.getProperty("java.io.tmpdir"));
//            repository.save(user);
//
//            System.out.println("USER PERSISTENCE TEST: " + retrieveValues(user.getUserName()));
//
//        }
//        if (RetrieveVals) {
//            retrieveValues(System.getProperty("user.name"));
//        }
//
//    }
//
//
//    private String checkSystemLevelAccess() {
//        try {
//            System.out.println("java.io.tmpdir :");
//            String tempDir = System.getProperty("java.io.tmpdir");
//            System.out.println(tempDir);
//
//            System.out.println("java.home :");
//            String home = System.getProperty("java.home");
//            System.out.println(home);
//
//            System.out.println("os.name :");
//            String osName = System.getProperty("os.name");
//            System.out.println(osName);
//
//            System.out.println("user.name :");
//            String userName = System.getProperty("user.name");
//            System.out.println(userName);
//
//            System.out.println("user.home :");
//            String homeDir = System.getProperty("user.home");
//            System.out.println(homeDir);
//
//            System.out.println("user.dir :");
//            String userDir = System.getProperty("user.dir");
//            System.out.println(userDir);
//            return success;
//        } catch (SecurityException | IllegalArgumentException | NullPointerException ex) {
//            ex.printStackTrace();
//            return failed;
//
//        }
//
//
//    }
//
//    private String retrieveValues(String userName) {
//        testUser = new User();
//        System.out.println(testUser.toString());
//        System.out.println(user.toString());
//        if (testUser.equals(user)) {
//            if (new File(testUser.getWriteDirectoryPath()).canWrite() &&
//                    new File(testUser.getDirectoryPath()).canRead()) {
//                return success;
//            }
//        }
//        return failed;
//    }
//
//    private static class TestResource {
//
//        private static final String PROJECT_SOURCE_DELETE_KEY = "Delete-On-Converted";
//        private static final String PROJECT_SOURCE_SAVE_KEY = "Save-On-Converted";
//
//
//    }
//}
