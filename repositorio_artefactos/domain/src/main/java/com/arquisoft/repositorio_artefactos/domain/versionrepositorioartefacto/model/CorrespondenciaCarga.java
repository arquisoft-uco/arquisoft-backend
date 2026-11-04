package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;

import java.util.UUID;

public record CorrespondenciaCarga(CargaPendienteDomain carga, UUID solicitante, UUID repositorio) {}
