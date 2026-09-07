package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record ResumenSolicitud(UUID solicitud, UUID remitenteUsuario, String tipoSolicitud) {}
