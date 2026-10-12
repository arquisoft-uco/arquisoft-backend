package com.arquisoft.solicitudes.domain.respuesta.model;

import java.util.UUID;

public record RespuestaSolicitud(UUID solicitud, boolean yaRespondida) {}
