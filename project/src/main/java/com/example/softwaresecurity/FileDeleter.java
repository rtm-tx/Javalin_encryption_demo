package com.example.softwaresecurity;

import java.io.File;


public class FileDeleter {
    public void deleteFile(String fileName) {
        File somefile = new File(fileName);
        boolean result = somefile.delete();
        if (!result) {
            System.err.println("File deletion failed. " + somefile.getName());
        } else {
            System.out.println("File " + somefile.getName() + " deleted successfully.");
        }
    }

    public static void main(String[] args) {
        FileDeleter fileDeleter = new FileDeleter();
        fileDeleter.deleteFile("c:\\somefile.txt");
    }
}
