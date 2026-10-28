package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;

import java.util.UUID;

public record ExistenciaCargaPendiente(UUID carga, CargaPendienteDomain encontrada) {}
