package ir.ac.aut.secondhand.frontend.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class MultipartBody {
    private final String boundary;
    private final ByteArrayOutputStream output;

    public MultipartBody() {
        this.boundary = "----SecondHandBoundary" + UUID.randomUUID();
        this.output = new ByteArrayOutputStream();
    }

    public MultipartBody addText(String name, String value) {
        if (value == null) {
            return this;
        }
        writeBoundary();
        write("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
        write("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
        write(value);
        write("\r\n");
        return this;
    }

    public MultipartBody addFile(String name, Path file) {
        if (file == null) {
            return this;
        }
        try {
            writeBoundary();
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            write("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
                    + file.getFileName() + "\"\r\n");
            write("Content-Type: " + contentType + "\r\n\r\n");
            output.write(Files.readAllBytes(file));
            write("\r\n");
            return this;
        } catch (IOException exception) {
            throw new ApiException("Unable to read image file: " + file, 0, exception);
        }
    }

    public byte[] build() {
        write("--" + boundary + "--\r\n");
        return output.toByteArray();
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    private void writeBoundary() {
        write("--" + boundary + "\r\n");
    }

    private void write(String value) {
        try {
            output.write(value.getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new ApiException("Unable to build multipart request", 0, exception);
        }
    }
}
