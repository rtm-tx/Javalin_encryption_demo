package com.example.softwaresecurity;

import io.javalin.Javalin;
import io.javalin.config.SizeUnit;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class Main {
    
    private static SecretKey aesSecretKey = generateAesKey();
    private static SecretKey blowfishSecretKey = generateBlowfishKey();
    private static SecretKey rc4SecretKey = generateRC4Key();  

    // Generate a secret key
    private static SecretKey generateAesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Secret key for Blowfish
    private static SecretKey generateBlowfishKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Secret key for RC4
    private static SecretKey generateRC4Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("RC4");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // AES Encrypt and decrypt text
    private static String encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    private static String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }

    // AES Encrypt and decrypt files
    private static byte[] fileEncrypt(byte[] fileInput) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
        return cipher.doFinal(fileInput);
    }
    private static byte[] fileDecrypt(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
        return cipher.doFinal(encrypted);
    }

    // Blowfish Encrypt and decrypt text
    private static String encryptBlowfish(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, blowfishSecretKey);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    private static String decryptBlowfish(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, blowfishSecretKey);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }

    // Blowfish Encrypt and decrypt files
    private static byte[] fileEncryptBlowfish(byte[] fileInput) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, blowfishSecretKey);
        return cipher.doFinal(fileInput);
    }
    private static byte[] fileDecryptBlowfish(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, blowfishSecretKey);
        return cipher.doFinal(encrypted);
    }

    // RC4 Encrypt and decrypt text
    private static String encryptRC4(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, rc4SecretKey);
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    private static String decryptRC4(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, rc4SecretKey);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }

    // RC4 Encrypt and decrypt files
    private static byte[] fileEncryptRC4(byte[] fileInput) throws Exception {
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, rc4SecretKey);
        return cipher.doFinal(fileInput);
    }
    private static byte[] fileDecryptRC4(byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, rc4SecretKey);
        return cipher.doFinal(encrypted);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.jetty.multipartConfig.cacheDirectory("\\temp");
            config.jetty.multipartConfig.maxFileSize(1, SizeUnit.MB); // 1MB file limit
        }).start(7000);
        app.before(ctx -> {
            ctx.header("X-Content-Type-Options", "nosniff");
            ctx.header("X-Frame-Options", "DENY");
            ctx.header("X-XSS-Protection", "1; mode=block");
            ctx.res().setCharacterEncoding("UTF-8");
        });

        // Main Page
        app.get("/home", ctx -> {
            StringBuilder html = new StringBuilder("<html><head><title>Encryption App</title></head><body>");
            html.append("<h1>Encryption Application</h1>");

            // JavaScript for file size validation
            html.append("<script>")
                .append("function validateFileSize() {")
                .append("    var fileInput = document.getElementById('fileInput');")
                .append("    if (fileInput.files.length > 0) {")
                .append("        var fileSize = fileInput.files[0].size;") // Size in bytes
                .append("        if (fileSize > 512000) {") // 500KB limit
                .append("            alert('File size exceeds limit of 500KB.');")
                .append("            fileInput.value = '';") // Clear the file input
                .append("            return false;")
                .append("        }")
                .append("    }")
                .append("    return true;")
                .append("}")
                .append("</script>")
                .append("</head><body>");

            // Form for textual input
            html.append("<form action='/encrypt-text' method='post'>")
                .append("<label for='text'>Enter text:</label>")
                .append("<input type='text' name='text'>")
                .append("<input type='submit' name='action' value='AES Encrypt'>")
                .append("<input type='submit' name='action' value='Blowfish Encrypt'>")
                .append("<input type='submit' name='action' value='RC4 Encrypt'>")
                .append("</form>");

            // Form for file upload with file size validation
            html.append("<form action='/encrypt-file' method='post' enctype='multipart/form-data' onsubmit='return validateFileSize()'>")
                .append("<label for='file'>Upload file:</label>")
                .append("<input type='file' id='fileInput' name='file' accept='.txt,.csv,.pdf,.jpg' onchange='validateFileSize()'>")
                .append("<input type='hidden' id='methodInput' name='method' value=''>") // Single hidden input for method
                .append("<input type='submit' name='action' value='AES Encrypt'>")
                .append("<input type='submit' name='action' value='Blowfish Encrypt'>")
                .append("<input type='submit' name='action' value='RC4 Encrypt'>")
                .append("</form>");

            html.append("</body></html>");
            ctx.html(html.toString());
        });

        // Encrypt Text
        app.post("/encrypt-text", ctx -> {
            String text = ctx.formParam("text");
            String action = ctx.formParam("action");            
            if (text != null && !text.isEmpty()) {
                String encryptedText;
                if ("Blowfish Encrypt".equals(action)) {
                    encryptedText = encryptBlowfish(text);
                } else if ("RC4 Encrypt".equals(action)) {
                    encryptedText = encryptRC4(text);
                } else {
                    encryptedText = encrypt(text);
                } 
        
                ctx.html("<html><body>Encrypted text: " + encryptedText +
                    "<form action='/decrypt-text' method='post'>" +
                    "<input type='hidden' name='encryptedText' value='" + encryptedText + "'>" +
                    "<input type='hidden' name='method' value='" + action + "'>" +
                    "<input type='submit' value='Decrypt'>" +
                    "</form></body></html>");
            } else {
                ctx.result("Invalid input.");
            }
        });

        // Decrypt Text, Tested to work with ALL characters
        app.post("/decrypt-text", ctx -> {
            String encryptedText = ctx.formParam("encryptedText");
            String method = ctx.formParam("method");
            if (encryptedText != null && !encryptedText.isEmpty()) {
                String decryptedText;
                try {
                    if ("Blowfish Encrypt".equals(method)) {
                        decryptedText = decryptBlowfish(encryptedText);
                    } else if ("RC4 Encrypt".equals(method)) {
                        decryptedText = decryptRC4(encryptedText);
                    } else {
                        decryptedText = decrypt(encryptedText);
                    }

                    ctx.html("<html><body><header><a href='/home'>Back to Home</a></header>" +
                        "<br>Decrypted Text: "+ decryptedText + "</body></html>");
                } catch (Exception e) {
                    ctx.result("Error decrypting text: " + e.getMessage());
                }
            } else {
                ctx.result("Invalid input.");
            }
        });

        // Confirmation Page for file upload
        app.post("/encrypt-file", ctx -> {
            UploadedFile file = ctx.uploadedFile("file");
            String action = ctx.formParam("action");
            // Preserve filename and extension
            String filename = file.filename();
            int extensionIndex = filename.lastIndexOf(".");
            if (extensionIndex != -1 && extensionIndex != filename.length() - 1) {
                filename = filename.substring(0, extensionIndex);
            }
            String extension = file.extension();
            if (file != null) {
                try (InputStream is = file.content()) {
                    byte[] fileContent = is.readAllBytes();
                    byte[] encryptedFile;
                    String encmethod;
                    if ("Blowfish Encrypt".equals(action)) {
                        encryptedFile = fileEncryptBlowfish(fileContent);
                        System.out.println("Encrytion method: Blowfish");
                        encmethod = "blowfish";
                    } else if ("RC4 Encrypt".equals(action)) {
                        encryptedFile = fileEncryptRC4(fileContent);
                        System.out.println("Encrytion method: RC4");
                        encmethod = "rc4";
                    } else {
                        encryptedFile = fileEncrypt(fileContent);
                        System.out.println("Encrytion method: AES");
                        encmethod = "aes";
                    }

                    ctx.html("<html><body>File encrypted successfully." +
                        "<form action='/decrypt-file' method='post'>" +
                        "<input type='hidden' name='encryptedContent' value='" + Base64.getEncoder().encodeToString(encryptedFile) + "'>" +
                        "<a href='/home'>Back to Home</a><br>" +
                        "<input type='submit' value='Download Decrypted File'><br>" +
                        "<input type='hidden' name='encmethod' value='" + encmethod + "'>" +
                        "<input type='hidden' name='filename' value='" + filename + "'>" +
                        "<input type='hidden' name='extension' value='" + extension + "'>" +
                        "Note: Please save the file with the appropriate extension after downloading." +
                        "</form></body></html>");
                } catch (IOException e) {
                    ctx.result("Error reading file: " + e.getMessage());
                } catch (Exception e) {
                    ctx.result("Error encrypting file: " + e.getMessage());
                }
            } else {
                ctx.result("No file uploaded.");
            }
        });

        // Decrypt File
        app.post("/decrypt-file", ctx -> {
            String encryptedContent = ctx.formParam("encryptedContent");
            String encmethod = ctx.formParam("encmethod");
            String filename = ctx.formParam("filename");
            String extension = ctx.formParam("extension");
            if (encryptedContent != null && !encryptedContent.isEmpty()) {
                try {
                    byte[] decryptedBytes;
                    System.out.println("Decryption method: " + encmethod);
                    if ("blowfish".equals(encmethod)) {
                        System.out.println("Using Blowfish for decryption");
                        decryptedBytes = fileDecryptBlowfish(Base64.getDecoder().decode(encryptedContent));
                    } else if ("rc4".equals(encmethod)) {
                        System.out.println("Using RC4 for decryption");
                        decryptedBytes = fileDecryptRC4(Base64.getDecoder().decode(encryptedContent));
                    } else {
                        System.out.println("Using AES for decryption");
                        decryptedBytes = fileDecrypt(Base64.getDecoder().decode(encryptedContent));
                    }

                    String contentType = "application/octet-stream";
                    String fileName = (encmethod + "_decrypt_" + filename + extension).replace(" ", "_");

                    ctx.res().setContentType(contentType);
                    ctx.res().setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                    ctx.res().getOutputStream().write(decryptedBytes);
                    ctx.res().getOutputStream().flush();
                    ctx.res().getOutputStream().close();
                } catch (Exception e) {
                    ctx.result("Error decrypting file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                ctx.result("Invalid input.");
            }
        });

    }
}