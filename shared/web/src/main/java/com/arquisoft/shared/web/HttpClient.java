package com.arquisoft.shared.web;

/**
 * Cliente HTTP para consumir APIs externas.
 * Los contextos usan esto para comunicarse con servicios externos.
 */
public interface HttpClient {
    /**
     * Realiza una petición GET.
     */
    <T> T get(String url, Class<T> responseType);

    /**
     * Realiza una petición POST.
     */
    <T> T post(String url, Object body, Class<T> responseType);

    /**
     * Realiza una petición PUT.
     */
    <T> T put(String url, Object body, Class<T> responseType);

    /**
     * Realiza una petición DELETE.
     */
    void delete(String url);
}
