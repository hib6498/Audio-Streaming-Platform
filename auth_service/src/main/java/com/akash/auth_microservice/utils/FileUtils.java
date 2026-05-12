package com.akash.auth_microservice.utils;

public class FileUtils {
  public static String getFileExtension(String fileName) {
    // Source - https://stackoverflow.com/a/3571239

    String extension = "";

    int i = fileName.lastIndexOf('.');
    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

    if (i > p) {
      extension = fileName.substring(i + 1);
    }

    return extension;
  }
}
