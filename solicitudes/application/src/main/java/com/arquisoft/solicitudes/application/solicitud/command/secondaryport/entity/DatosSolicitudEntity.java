package com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity;

import java.util.UUID;

public record DatosSolicitudEntity(
        UUID remitenteUsuario, UUID destinatarioUsuario, String tipoSolicitud) {
}
