package ru.netology;

public class Request {

    private final RequestLine requestLine;
    private final String headers;
    private String body;

    public Request(RequestLine requestLine, String headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public Request(RequestLine requestLine, String headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}