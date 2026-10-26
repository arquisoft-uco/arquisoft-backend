package com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;

public final class ArtefactoMapper {

    private ArtefactoMapper() {}

    public static ArtefactoEntity toEntity(ArtefactoDomain artefacto) {
        return new ArtefactoEntity(
                artefacto.getId(),
                artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(),
                artefacto.getAsesor());
    }

    public static ArtefactoDomain toDomain(ArtefactoEntity entity) {
        return ArtefactoDomain.reconstruir(
                entity.id(),
                entity.proyectoGrado(),
                entity.versionRepositorioArtefacto(),
                entity.asesor());
    }
}
