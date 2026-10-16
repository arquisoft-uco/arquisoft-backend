package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;

public record CoincidenciaTipoArchivo(String nombreArchivo, String tipoAlmacenado, AnalisisArchivoDomain analisis) {}
