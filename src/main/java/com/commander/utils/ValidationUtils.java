package com.commander.utils;

import com.commander.model.User;

/**
 * Utility class for validating object fields are initialized
 *
 * @author HGDIV
 */
public class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * @param user The current User
     * @return true if fields have been initialized
     */
    public static boolean validateUserPaths(User user) {
        return ((user.getSourceFilePolicy() != null) &&
                (user.getImgPreference() != null) &&
                (user.getWriteDirectoryPath() != null) &&
                (user.getDirectoryPath() != null) &&
                (user.getDocPreference() != null) &&
                (user.getExcelPreference() != null) &&
                (user.getReplaceBgColor() != null));
    }

    /**
     * @param user The current User
     * @return true if User fields are initialized
     */
    public static boolean validateUser(User user) {
        return ((user.getDocPreference() != null) & (user.getExcelPreference() != null))
                && ((user.getImgPreference() != null) & (user.getDirectoryPath() != null))
                && ((user.getWriteDirectoryPath() != null) & (user.getSourceFilePolicy() != null));

    }


}
