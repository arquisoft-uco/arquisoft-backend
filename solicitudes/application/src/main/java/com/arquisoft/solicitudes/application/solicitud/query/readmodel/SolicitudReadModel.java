package com.arquisoft.solicitudes.application.solicitud.query.readmodel;

import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitudReadModel(
        UUID id,
        String mensajeSolicitud,
        LocalDateTime fechaCreacion,
        String tipoSolicitudId,
        String tipoSolicitudNombre,
        RemitenteReadModel remitente,
        DestinatarioReadModel destinatario
) {
}
