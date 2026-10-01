package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de MinioGuia. */
public enum MinioGuiaKey implements ClaveMensaje {

    LOG_UPLOAD_URL("fichas.infraestructura.minioguia.log.upload-url", 2),
    LOG_DOWNLOAD_URL("fichas.infraestructura.minioguia.log.download-url", 2),
    LOG_DELETE("fichas.infraestructura.minioguia.log.delete", 2);

    private final String clave;
    private final int parametros;

    MinioGuiaKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
