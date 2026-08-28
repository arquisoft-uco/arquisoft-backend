package com.arquisoft.solicitudes.domain.solicitud.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClaveSolicitud(
        UUID destinatario,
        UUID remitente,
        LocalDateTime fechaCreacion,
        String mensajeSolicitud) {}
