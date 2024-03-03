package com.example.softwaresecurity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionExample {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide a file name as a command line argument.");
            return;
        }
        
        try {
            // Linux stores a user's home directory in the environment variable $HOME, while Windows uses %APPDATA%
            FileInputStream fis = new FileInputStream(System.getenv("APPDATA") + File.separator + args[0]);
            // Close the FileInputStream
            fis.close();
        } catch (FileNotFoundException e) {
            // Log the exception
            System.err.println("An error occurred: " + e.getMessage());
            
            // throw the exception as a more general one
            throw new RuntimeException("Unable to access the requested file.");
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
    }
}
