package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record RespuestasSolicitud(UUID solicitud, boolean tieneRespuestas) {}
