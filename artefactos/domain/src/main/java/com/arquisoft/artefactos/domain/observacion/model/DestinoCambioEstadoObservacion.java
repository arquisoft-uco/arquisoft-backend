package com.arquisoft.artefactos.domain.observacion.model;

import java.util.UUID;

public record DestinoCambioEstadoObservacion(UUID observacion, EstadoObservacion estado) {}
