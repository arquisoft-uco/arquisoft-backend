package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.entity.ArchivoVersionArtefactoEntity;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;

public final class ArchivoVersionArtefactoMapper {

    private ArchivoVersionArtefactoMapper() {}

    public static ArchivoVersionArtefactoDomain toDomain(ArchivoVersionArtefactoEntity entity) {
        return ArchivoVersionArtefactoDomain.reconstruir(
                entity.asesor(), entity.uriArchivo(), entity.numeroVersion(), entity.nombreDocumento());
    }
}
