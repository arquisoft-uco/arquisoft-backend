package com.arquisoft.shared.minio.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.AlmacenamientoKey;
import com.arquisoft.shared.minio.exception.MinioOperationException;

import java.io.IOException;
import java.io.InputStream;

public record LecturaObjeto(InputStream contenido, String etag) implements AutoCloseable {

    @Override
    public void close() {
        try {
            contenido.close();
        } catch (IOException e) {
            throw new MinioOperationException(
                    Mensajes.obtener(AlmacenamientoKey.ERROR_LECTURA),
                    AppCodes.Minio.LECTURA_FALLIDA,
                    e);
        }
    }
}
