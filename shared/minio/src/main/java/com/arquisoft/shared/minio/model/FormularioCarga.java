package com.arquisoft.shared.minio.model;

import java.time.Instant;
import java.util.Map;

public record FormularioCarga(String url, Map<String, String> campos, Instant expiraEn) {
}
