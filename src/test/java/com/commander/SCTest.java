package com.commander;

import com.commander.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.imageio.ImageIO;
import java.util.Arrays;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SCTest extends JavaFxSpringApplication {

    org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SCTest.class);


    @Value("${application.test.inputpath}")
    String fileInputTestPath;

    @Value("${application.test.outputpath}")
    String fileOutputTestPath;

    User user;

    final static String success = "Success";
    final static String failed = "Failed";

    boolean SystemLevelAccessTest = false;




    @Test
    void contextLoads() {
        if (SystemLevelAccessTest) {
            LOGGER.info("SYSTEM LEVEL ACCESS TEST WAS SUCCESSFUL: " + checkSystemLevelAccess());

        }

        }


    private String checkSystemLevelAccess() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            LOGGER.info("java.io.tmpdir : " + tempDir );

            String home = System.getProperty("java.home");
            LOGGER.info("java.home : " + home);

            String osName = System.getProperty("os.name");
            LOGGER.info("os.name : " + osName);

            String userName = System.getProperty("user.name");
            LOGGER.info("user.name : " + userName);

            String homeDir = System.getProperty("user.home");
            LOGGER.info("user.home : " + homeDir);

            String userDir = System.getProperty("user.dir");
            LOGGER.info("user.dir :" + userDir);

            return success;

        } catch (SecurityException | IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            return failed;

        }


    }


    private static class TestResource {

        private static final String PROJECT_SOURCE_DELETE_KEY = "Delete";
        private static final String PROJECT_SOURCE_SAVE_KEY = "Save";


    }
       private void printImgCompat(){
          System.out.println("Image Reader Names : " + Arrays.toString(ImageIO.getReaderFormatNames()));
        System.out.println("Image Reader Names : " + Arrays.toString(ImageIO.getWriterFormatNames()));

 }
 @Configuration
 static class ConfigTest {

 }
}
