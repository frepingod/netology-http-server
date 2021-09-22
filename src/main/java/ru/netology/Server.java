package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                threadPool.submit(new ServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        Map<String, Handler> map = new ConcurrentHashMap<>();
        if (handlers.containsKey(method)) {
            map = handlers.get(method);
        }
        map.put(path, handler);
        handlers.put(method, map);
    }

    private class ServerHandler implements Runnable {

        private final Socket socket;

        public ServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
            ) {
                final String[] parts = in.readLine().split(" ");
                if (parts.length != 3) {
                    return;
                }
                RequestLine requestLine = new RequestLine(parts[0], parts[1], parts[2]);

                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = in.readLine()) != null) {
                    sb.append(str);
                }
                String[] headersAndBody = sb.toString().split("\r\n\r\n");

                Request request = headersAndBody.length != 2 ? new Request(requestLine, headersAndBody[0]) :
                        new Request(requestLine, headersAndBody[0], headersAndBody[1]);

                Handler handler = handlers.get(request.getRequestLine().getMethod())
                        .get(request.getRequestLine().getPath());
                handler.handle(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}