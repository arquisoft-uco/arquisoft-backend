package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.dto;

import com.arquisoft.solicitudes.infrastructure.destinatario.query.primaryadapter.web.dto.DestinatarioResponseDTO;
import com.arquisoft.solicitudes.infrastructure.remitente.query.primaryadapter.web.dto.RemitenteResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitudResponseDTO(
        UUID id,
        String mensajeSolicitud,
        LocalDateTime fechaCreacion,
        String tipoSolicitudId,
        String tipoSolicitudNombre,
        RemitenteResponseDTO remitente,
        DestinatarioResponseDTO destinatario
) {
}
