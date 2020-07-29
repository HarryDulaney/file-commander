package com.commander.model;

/**
 * A document type that is eligible for
 * converting
 */
public enum DocType {
    DOCX(".docx", "docx"),
    PDF(".pdf", "pdf"),
    TXT(".txt", "txt"),
    XLSX(".xlsx", "xlsx"),
    CSV(".csv", "csv"),
    HTML(".html", "html"),
    BMP(".bmp", "bmp"),
    PNG(".png", "png"),
    GIF(".gif", "gif"),
    JPG(".jpg", "jpg");


    private final String id;
    private final String extension;


    DocType(final String extension,final String id) {
        this.extension = extension;
        this.id = id;
    }


    public String getExtension() {
        return extension;

    }

    public String getId() {
        return id;
    }

    public static String docx() {
        return ".docx";

    }

    public static String pdf() {
        return ".pdf";

    }

    public static String txt() {
        return ".txt";
    }

}
