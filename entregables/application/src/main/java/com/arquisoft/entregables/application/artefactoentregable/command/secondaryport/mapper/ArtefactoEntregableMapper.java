package com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.mapper;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;

public final class ArtefactoEntregableMapper {

    private ArtefactoEntregableMapper() {}

    public static ArtefactoEntregableEntity toEntity(ArtefactoEntregableDomain artefacto) {
        return new ArtefactoEntregableEntity(
                artefacto.getId(),
                artefacto.getVersionArtefacto(),
                artefacto.getEntregableProyectoGrado(),
                artefacto.getUriArchivo(),
                artefacto.getArtefacto(),
                artefacto.getVersionRepositorioArtefacto(),
                artefacto.getNombreDocumento(),
                artefacto.getRepositorio(),
                artefacto.getNombreRepositorio());
    }
}
