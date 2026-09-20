package com.arquisoft.solicitudes.application.respuesta.query.readmodel;

import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record RespuestaReadModel(
        UUID id,
        String contenido,
        LocalDateTime fechaRespuesta,
        String estadoRespuestaId,
        String estadoRespuestaNombre,
        SolicitudReadModel solicitud
) {
}
