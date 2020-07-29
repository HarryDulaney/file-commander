package com.commander.model;

public enum DocOperation {

    DOCX_TO_PDF("docx -> pdf", ".docx"),
    DOCX_TO_HTML("docx -> html", ".docx"),
    HTML_TO_DOCX("html -> docx", ".html"),
    PDF_TO_TEXT("pdf -> (EXTRACT TEXT) -> docx", ".pdf");


    private final String operation;
    private final String sourceFileExtention;

    DocOperation(String operation, String sourceFileExtention) {

        this.operation = operation;
        this.sourceFileExtention = sourceFileExtention;
    }

    public String getDocOperation() {
        return operation;
    }

    public String getSourceFileExt() {
        return sourceFileExtention;
    }
}
