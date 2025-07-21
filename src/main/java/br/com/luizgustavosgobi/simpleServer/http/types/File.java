package br.com.luizgustavosgobi.simpleServer.http.types;

import java.io.FileOutputStream;
import java.io.IOException;

public record File(String fileName, byte[] data, String contentType) {

    public java.io.File write(String path, String fileName) throws IOException {
        java.io.File file = new java.io.File(path, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }

        return file;
    }
    public java.io.File write(String path) throws IOException {
        java.io.File file = new java.io.File(path, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }

        return file;
    }

}
