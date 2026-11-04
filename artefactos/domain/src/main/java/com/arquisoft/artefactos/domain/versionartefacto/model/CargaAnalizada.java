package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;

public record CargaAnalizada(CargaPendienteDomain carga, AnalisisArchivoDomain analisis) {}
