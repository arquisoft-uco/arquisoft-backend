package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.dto;

import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.dto.SolicitudResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaResponseDTO(
        UUID id,
        String contenido,
        LocalDateTime fechaRespuesta,
        String estadoRespuestaId,
        String estadoRespuestaNombre,
        SolicitudResponseDTO solicitud
) {
}
