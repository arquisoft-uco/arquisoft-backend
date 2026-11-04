package com.arquisoft.shared.minio.model;

import java.util.Map;

public record MetadatosObjeto(long tamano, String tipoContenido, String etag, Map<String, String> metadatos) {

    public static final MetadatosObjeto VACIO = new MetadatosObjeto(0L, "", "", Map.of());

    public boolean esVacio() {
        return this == VACIO;
    }
}
