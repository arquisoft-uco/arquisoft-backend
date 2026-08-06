package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de MinioGuia. */
public enum MinioGuiaKey implements MessageKey {

    LOG_UPLOAD_URL("fichas.infraestructura.minioguia.log.upload-url"),
    LOG_DOWNLOAD_URL("fichas.infraestructura.minioguia.log.download-url"),
    LOG_DELETE("fichas.infraestructura.minioguia.log.delete");

    private final String clave;

    MinioGuiaKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.FICHAS;
    }
}
