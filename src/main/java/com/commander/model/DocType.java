package com.commander.model;

/**
 * Represents a document type that is eligible for
 * converting
 */
public enum DocType {
    DOCX(".docx"),
    PDF(".pdf"),
    TXT(".txt");

    private String extension;

    DocType(String extension) {
        this.extension = extension;
    }


    public String getExtension() {
        return extension;

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
