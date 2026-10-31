package com.arquisoft.artefactos.domain.observacion.model;

import java.util.UUID;

public record EstadoActualObservacion(UUID observacion, EstadoObservacion estado) {}
