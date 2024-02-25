package com.example.softwaresecurity;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static void main(String[] args) {
        // Webpage Instantiation on port 7000
        Javalin app = Javalin.create().start(7000);
        app.before(ctx -> {
            ctx.header("X-Content-Type-Options", "nosniff");
            ctx.header("X-Frame-Options", "DENY");
            ctx.header("X-XSS-Protection", "1; mode=block");
            ctx.res().setCharacterEncoding("UTF-8");
        });

        // Main Page
        app.get("/home", ctx -> {
            StringBuilder html = new StringBuilder("<html><head><title>Encryption App</title></head><body>");
            html.append("<h1>MD5 Application</h1>");

            // Form for text input
            html.append("<form action='/encrypt-text' method='post'>")
                .append("<label for='text'>Enter text:</label>")
                .append("<input type='text' name='text'>")
                .append("<input type='submit' value='Encrypt'>")
                .append("</form>");

            // Form for file upload
            html.append("<form action='/encrypt-file' method='post' enctype='multipart/form-data'>")
                .append("<label for='file'>Upload file:</label>")
                .append("<input type='file' name='file'>")
                .append("<input type='submit' value='Encrypt'>")
                .append("</form>");

            html.append("</body></html>");
            ctx.html(html.toString());
        });

        // Text to MD5 hash
        app.post("/encrypt-text", ctx -> {
            String text = ctx.formParam("text");
            if (text != null && !text.isEmpty()) {
                String md5Hash = generateMD5Hash(text.getBytes(StandardCharsets.UTF_8));
                ctx.result("MD5 hash of text: " + md5Hash);
            } else {
                ctx.result("Invalid input.");
            }
        });

        // File to MD5 hash
        app.post("/encrypt-file", ctx -> {
            UploadedFile file = ctx.uploadedFile("file");
            if (file != null) {
                try (InputStream is = file.content()) {
                    byte[] fileContent = is.readAllBytes();
                    String md5Hash = generateMD5Hash(fileContent);
                    ctx.result("MD5 hash of uploaded file: " + md5Hash);
                } catch (IOException e) {
                    ctx.result("Error reading file: " + e.getMessage());
                }
            } else {
                ctx.result("No file uploaded.");
            }
        });
    }
    // MD5 Hash Generation
    private static String generateMD5Hash(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "MD5 algorithm not found";
        }
    }
}