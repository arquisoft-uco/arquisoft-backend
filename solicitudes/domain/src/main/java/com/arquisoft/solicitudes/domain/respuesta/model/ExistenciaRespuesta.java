package com.arquisoft.solicitudes.domain.respuesta.model;

import java.util.UUID;

public record ExistenciaRespuesta(UUID solicitud, boolean existe) {}
