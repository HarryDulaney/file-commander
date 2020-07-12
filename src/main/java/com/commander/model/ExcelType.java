package com.commander.model;

public enum ExcelType {
    CSV(".csv"),
    XLSX(".xlsx");

    private String extension;


    ExcelType(String extension) {
        this.extension = extension;
    }


    public String getExtension() {
        return extension;
    }
    public static String csv(){
        return ".csv";
    }
    public static String xlsx(){
        return ".xlsx";
    }


}


