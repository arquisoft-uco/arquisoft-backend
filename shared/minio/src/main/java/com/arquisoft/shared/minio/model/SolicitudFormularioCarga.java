package com.arquisoft.shared.minio.model;

import java.util.Map;

public record SolicitudFormularioCarga(
        String bucket,
        String key,
        String tipoContenido,
        long tamanoMaximoBytes,
        Map<String, String> metadatos) {
}
