package com.arquisoft.shared.web.client;

public interface HttpClient {
    <T> T get(String url, Class<T> responseType);

    <T> T post(String url, Object body, Class<T> responseType);

    <T> T put(String url, Object body, Class<T> responseType);

    void delete(String url);
}
