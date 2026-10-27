package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;

public final class AnalisisArchivoMapper {

    private AnalisisArchivoMapper() {}

    public static AnalisisArchivoDomain toDomain(AnalisisArchivoEntity entity) {
        return AnalisisArchivoDomain.reconstruir(entity.tipoDetectado(), entity.etag());
    }
}
