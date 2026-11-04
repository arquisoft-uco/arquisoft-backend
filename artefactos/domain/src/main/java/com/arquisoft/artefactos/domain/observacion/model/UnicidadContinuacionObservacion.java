package com.arquisoft.artefactos.domain.observacion.model;

import java.util.UUID;

public record UnicidadContinuacionObservacion(EstadoObservacion destino, UUID revisionAsesor, boolean existe) {}
