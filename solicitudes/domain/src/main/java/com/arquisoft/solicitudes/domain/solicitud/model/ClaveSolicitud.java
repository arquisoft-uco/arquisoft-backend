package com.arquisoft.solicitudes.domain.solicitud.model;

import java.time.Instant;
import java.util.UUID;

public record ClaveSolicitud(
        UUID destinatario,
        UUID remitente,
        Instant fechaCreacion,
        String mensajeSolicitud) {}
