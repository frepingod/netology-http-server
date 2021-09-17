package ru.netology;

public class Main {

    public static void main(String[] args) {
        int port = 9999;

        Server server = new Server();
        server.listen(port);
    }
}


