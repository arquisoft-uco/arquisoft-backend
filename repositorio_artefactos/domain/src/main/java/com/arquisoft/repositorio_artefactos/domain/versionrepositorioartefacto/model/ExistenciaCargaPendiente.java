package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;

import java.util.UUID;

public record ExistenciaCargaPendiente(UUID carga, CargaPendienteDomain encontrada) {}
