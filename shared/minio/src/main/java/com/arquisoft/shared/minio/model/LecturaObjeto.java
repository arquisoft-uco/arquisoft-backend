package com.arquisoft.shared.minio.model;

import com.arquisoft.shared.minio.exception.MinioOperationException;

import java.io.IOException;
import java.io.InputStream;

public record LecturaObjeto(InputStream contenido, String etag) implements AutoCloseable {

    @Override
    public void close() {
        try {
            contenido.close();
        } catch (IOException e) {
            throw MinioOperationException.lecturaFallida(e);
        }
    }
}
