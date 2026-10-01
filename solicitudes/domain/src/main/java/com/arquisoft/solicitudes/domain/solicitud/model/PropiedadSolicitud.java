package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record PropiedadSolicitud(UUID solicitud, UUID remitenteUsuario, UUID solicitante) {}
