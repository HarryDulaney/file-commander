package com.commander.model;

/**
 * A document type that is eligible for
 * converting
 */
public enum DocType {
    DOCX(".docx", "docx"),
    PDF(".pdf", "pdf"),
    XLSX(".xlsx", "xlsx"),
    CSV(".csv", "csv"),
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
    public static final String BMP_ID = "bmp";
    public static final String PNG_ID = "png";
    public static final String JPG_ID = "jpg";
    public static final String GIF_ID = "gif";

    DocType(final String extension, final String id) {
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
        return DOCX.extension;

    }

    public static String pdf() {
        return PDF.extension;

    }

    public static String csv() {
        return CSV.extension;

    }

    public static String xlsx() {
        return XLSX.extension;

    }

    public static String jpg() {
        return JPG.extension;
    }

    public static String png() {
        return PNG.extension;

    }

    public static String gif() {
        return GIF.extension;

    }

    public static String bmp() {
        return BMP.extension;
    }

}
