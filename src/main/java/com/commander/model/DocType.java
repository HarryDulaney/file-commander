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
    public static final String CSV_ID = "csv";
    public static final String XLSX_ID = "xlsx";
    public static final String DOCX_ID = "docx";
    public static final String PDF_ID = "pdf";
    public static final String TXT_ID = "txt";
    public static final String HTML_ID = "html";
    public static final String BMP_ID = "bmp";
    public static final String PNG_ID = "png";
    public static final String JPG_ID = "jpg";
    public static final String GIF_ID = "gif";

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
