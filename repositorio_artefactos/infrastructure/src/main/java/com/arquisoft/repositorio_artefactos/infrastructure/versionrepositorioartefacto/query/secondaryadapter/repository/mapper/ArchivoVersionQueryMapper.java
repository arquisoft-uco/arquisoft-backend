package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.entity.ArchivoVersionEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.ArchivoVersionJpaQueryEntity;

public final class ArchivoVersionQueryMapper {

    private ArchivoVersionQueryMapper() {}

    public static ArchivoVersionEntity toEntity(ArchivoVersionJpaQueryEntity entity) {
        return new ArchivoVersionEntity(entity.getUriArchivo(), entity.getNombreArchivo());
    }
}
