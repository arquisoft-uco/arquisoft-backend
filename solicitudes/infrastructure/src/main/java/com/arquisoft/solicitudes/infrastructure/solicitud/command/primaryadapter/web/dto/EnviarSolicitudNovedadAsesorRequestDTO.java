package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto;

public record EnviarSolicitudNovedadAsesorRequestDTO(
        String destinatario,
        String mensajeSolicitud) {}
