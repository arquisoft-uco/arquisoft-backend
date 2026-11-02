package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.entity.ArchivoVersionEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;

public final class ArchivoVersionMapper {

    private ArchivoVersionMapper() {}

    public static ArchivoVersionDomain toDomain(ArchivoVersionEntity entity) {
        return ArchivoVersionDomain.reconstruir(entity.uriArchivo(), entity.nombreArchivo());
    }
}
