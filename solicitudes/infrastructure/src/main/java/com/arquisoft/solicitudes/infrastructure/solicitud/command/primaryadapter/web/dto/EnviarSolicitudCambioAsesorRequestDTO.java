package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto;

public record EnviarSolicitudCambioAsesorRequestDTO(
        String destinatario,
        String mensajeSolicitud) {}
