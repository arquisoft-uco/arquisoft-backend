package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto;

public record EnviarSolicitudNovedadCoordinadorRequestDTO(
        String destinatario,
        String mensajeSolicitud) {}
