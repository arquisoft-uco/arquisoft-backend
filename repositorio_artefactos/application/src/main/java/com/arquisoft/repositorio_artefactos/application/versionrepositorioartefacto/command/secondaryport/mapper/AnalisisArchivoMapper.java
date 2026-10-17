package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;

public final class AnalisisArchivoMapper {

    private AnalisisArchivoMapper() {}

    public static AnalisisArchivoDomain toDomain(AnalisisArchivoEntity entity) {
        return AnalisisArchivoDomain.reconstruir(entity.tipoDetectado(), entity.hashSha256(), entity.etag());
    }
}
