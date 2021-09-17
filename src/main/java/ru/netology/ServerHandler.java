package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ServerHandler implements Runnable {

    private final Socket socket;

    final List<String> validPaths = List.of(
            "/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
            "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js"
    );

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {

            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final String requestLine = in.readLine();
            final String[] parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            final String path = parts[1];
            if (!validPaths.contains(path)) {
                out.write(response("404 Not Found", "", 0));
                out.flush();
                return;
            }

            final Path filePath = Path.of(".", "public", path);
            final String mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final String template = Files.readString(filePath);
                final byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write(response("200 OK", mimeType, content.length));
                out.write(content);
                out.flush();
                return;
            }

            final long length = Files.size(filePath);
            out.write(response("200 OK", mimeType, length));
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] response(String statusCode, String contentType, long contentLength) {
        return (
                "HTTP/1.1 " + statusCode +"\r\n" +
                        (!contentType.isEmpty() ? "Content-Type: " + contentType + "\r\n" : "") +
                        "Content-Length: " + contentLength + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes();
    }
}