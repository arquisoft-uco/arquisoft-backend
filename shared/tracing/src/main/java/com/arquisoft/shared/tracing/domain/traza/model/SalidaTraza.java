package com.arquisoft.shared.tracing.domain.traza.model;

import java.time.Instant;

public record SalidaTraza(int codigoEstado, Instant tiempoSalida, long duracionMs) {
}
