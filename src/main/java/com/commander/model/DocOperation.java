package com.commander.model;

public enum DocOperation {

    DOCX_TO_PDF("docx -> pdf", ".pdf"),
    DOCX_TO_HTML("docx -> html", ".html"),
    HTML_TO_DOCX("html -> docx", ".docx"),
    PDF_txt_TO_DOCX("pdf -> (EXTRACT TEXT) -> docx", ".docx"),
    PDF_TO_DOCX("pdf -> docx (Windows Only)",".docx");


    private final String operation;
    /**
     * Used to evaluate if a source file is eligible for the DocOperation
     */
    private final String preferenceFileExtension;

    DocOperation(String operation, String preferenceFileExtension) {

        this.operation = operation;
        this.preferenceFileExtension = preferenceFileExtension;
    }

    public String getDocOperation() {
        return operation;
    }

    public String getSourceFileExt() {
        return preferenceFileExtension;
    }
}
