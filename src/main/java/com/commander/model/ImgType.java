package com.commander.model;

public enum ImgType {
    BMP(".bmp"),
    PNG(".png"),
    GIF(".gif"),
    JPG(".jpg");

    private String extension;

    ImgType(String extension) {
        this.extension = extension;

    }

    public String getExtension() {
        return extension;

    }

    public static String bmp() {
        return ".bmp";

    }

    public static String png() {
        return ".png";
    }

    public static String jpg() {
        return ".jpg";
    }

    public static String gif() {
        return ".gif";
    }


}
