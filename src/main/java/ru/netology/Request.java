package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Request {

    private final RequestLine requestLine;
    private final List<String> headers;
    private final List<NameValuePair> queryParams;
    private String body;
    private List<NameValuePair> postParams;

    public Request(RequestLine requestLine, List<String> headers) throws URISyntaxException {
        this.requestLine = requestLine;
        this.headers = headers;
        queryParams = URLEncodedUtils.parse(new URI(requestLine.getPath()), StandardCharsets.UTF_8);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Optional<String> getHeader(String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getQueryParam(String name) {
        return getParam(queryParams, name);
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPostParam(String name) {
        return getParam(postParams, name);
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }

    private List<String> getParam(List<NameValuePair> params, String name) {
        return params.stream()
                .filter(o -> o.getName().startsWith(name))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
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