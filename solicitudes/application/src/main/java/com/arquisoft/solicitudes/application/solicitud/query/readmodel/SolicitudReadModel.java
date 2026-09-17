package com.arquisoft.solicitudes.application.solicitud.query.readmodel;

import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;

import java.time.Instant;
import java.util.UUID;

public record SolicitudReadModel(
        UUID id,
        String mensajeSolicitud,
        Instant fechaCreacion,
        String tipoSolicitudId,
        String tipoSolicitudNombre,
        RemitenteReadModel remitente
) {
}
