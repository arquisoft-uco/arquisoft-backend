package com.arquisoft.solicitudes.domain.respuesta.model;

import java.util.UUID;

public record NuevoEstadoRespuesta(UUID solicitud, String nuevoEstado) {}
