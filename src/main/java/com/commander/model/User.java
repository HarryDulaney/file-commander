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
     * First time User with no persistant preferences
     */
    private Boolean nuUser;

    private ExcelType excelPreference;
    private DocType docPreference;
    private ImgType imgPreference;


    public User() {

    }

    public User(ExcelType e, DocType d, ImgType i) {

            excelPreference = e;
            docPreference = d;
            imgPreference = i;
    }


    public ExcelType getExcelPreference() {
        return excelPreference;
    }

    public void setExcelPreference(ExcelType excelPreference) {
        this.excelPreference = excelPreference;
    }

    public DocType getDocPreference() {
        return docPreference;
    }

    public void setDocPreference(DocType docPreference) {
        this.docPreference = docPreference;
    }

    public ImgType getImgPreference() {
        return imgPreference;
    }

    public void setImgPreference(ImgType imgPreference) {
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
                    && ((User)obj).getSourceFilePolicy().equals(this.getSourceFilePolicy())
                    && this.excelPreference.equals(((User) obj).getExcelPreference())
                    && this.docPreference.equals(((User) obj).getDocPreference())
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