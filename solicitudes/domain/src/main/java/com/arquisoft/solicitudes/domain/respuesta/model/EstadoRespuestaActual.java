package com.arquisoft.solicitudes.domain.respuesta.model;

import java.util.UUID;

public record EstadoRespuestaActual(UUID solicitud, String estado) {}
