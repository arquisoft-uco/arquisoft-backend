package com.arquisoft.entregables.domain.artefacto.model;

import java.util.UUID;

public record ExistenciaArchivoOrigenArtefacto(UUID artefacto, String nombreDocumento, String claveOrigen, boolean existe) {}
