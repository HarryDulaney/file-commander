package com.commander.model;

import org.springframework.stereotype.Component;

/**
 * <p>
 * User is a well encapsulated POJO
 * that represents the application users properties, i.e. Id,
 * username, references, file extension type preferences.
 * </p>
 *
 * @author HGDIV
 */

@Component
public class User {

    private static final String CURRENT_USER_ID = System.getProperty("user.name");

    private String directoryPath;
    private String writeDirectoryPath;
    private String sourceFilePolicy;

    /**
     * First time User with no persistent preferences
     */
    private Boolean nuUser;

    private DocType excelPreference;
    private DocOperation docPreference;
    private DocType imgPreference;


    public User() {
        super();

    }

    public User(DocType excelPreference, DocOperation docPreference, DocType imgPreference) {

            this.excelPreference = excelPreference;
            this.docPreference = docPreference;
            this.imgPreference = imgPreference;
    }


    public DocType getExcelPreference() {
        return excelPreference;
    }

    public void setExcelPreference(DocType excelPreference) {
        this.excelPreference = excelPreference;
    }

    public DocOperation getDocPreference() {
        return docPreference;
    }

    public void setDocPreference(DocOperation docPreference) {
        this.docPreference = docPreference;
    }

    public DocType getImgPreference() {
        return imgPreference;
    }

    public void setImgPreference(DocType imgPreference) {
        this.imgPreference = imgPreference;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getSourceFilePolicy() {
        return sourceFilePolicy;
    }

    public void setSourceFilePolicy(String sourceFilePolicy) {
        this.sourceFilePolicy = sourceFilePolicy;
    }

    public String getWriteDirectoryPath() {
        return writeDirectoryPath;
    }

    public void setWriteDirectoryPath(String writeDirectoryPath) {
        this.writeDirectoryPath = writeDirectoryPath;
    }
    public Boolean getNuUser() {
        return nuUser;
    }

    public void setNuUser(Boolean nuUser) {
        this.nuUser = nuUser;
    }


    /**
     * @return A String who's contents can be used to uniquely identify
     * the current user of the application.
     */
    public static String getCurrentUserId() {
        return CURRENT_USER_ID;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).getDirectoryPath().equals(this.getDirectoryPath())
                    && ((User) obj).getWriteDirectoryPath().equals(this.getWriteDirectoryPath())
                    && ((User) obj).getSourceFilePolicy().equals(this.getSourceFilePolicy())
                    && this.excelPreference.equals(((User)obj).getExcelPreference())
                    && this.docPreference.equals(((User)obj).getDocPreference())
                    && this.imgPreference.equals(((User)obj).getImgPreference());
        } else
            return false;
    }

    @Override
    public String toString() {
        return "User{" +
                ", userName='" + CURRENT_USER_ID + '\'' +
                ", directoryPath='" + directoryPath + '\'' +
                ", writeDirectoryPath='" + writeDirectoryPath + '\'' +
                ", sourceFilePolicy='" + sourceFilePolicy + '\'' +
                ", execPref='" + getExcelPreference().toString() + '\'' +
                ", docPref='" + getDocPreference().toString() + '\'' +
                ", imgPref='" + getImgPreference().toString() + '\'' +
                '}';
    }

}