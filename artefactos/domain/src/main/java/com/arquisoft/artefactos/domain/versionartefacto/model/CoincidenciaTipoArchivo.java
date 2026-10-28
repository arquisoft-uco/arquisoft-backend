package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;

public record CoincidenciaTipoArchivo(String nombreArchivo, String tipoAlmacenado, AnalisisArchivoDomain analisis) {}
