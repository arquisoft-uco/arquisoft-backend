package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;

public final class ArtefactoJpaMapper {

    private ArtefactoJpaMapper() {}

    public static ArtefactoJpaEntity toJpaEntity(ArtefactoEntity entity) {
        return ArtefactoJpaEntity.builder()
                .id(entity.id())
                .proyectoGrado(entity.proyectoGrado())
                .versionArtefacto(entity.versionArtefacto())
                .numeroVersion(entity.numeroVersion())
                .fechaVersion(entity.fechaVersion())
                .claveObjeto(entity.claveObjeto())
                .versionRepositorioArtefacto(entity.versionRepositorioArtefacto())
                .nombreDocumento(entity.nombreDocumento())
                .repositorio(entity.repositorio())
                .nombreRepositorio(entity.nombreRepositorio())
                .omitidoEn(entity.omitidoEn())
                .omisionOcurridaEn(entity.omisionOcurridaEn())
                .build();
    }

    public static ArtefactoEntity toEntity(ArtefactoJpaEntity jpa) {
        return new ArtefactoEntity(
                jpa.getId(),
                jpa.getProyectoGrado(),
                jpa.getVersionArtefacto(),
                jpa.getNumeroVersion(),
                jpa.getFechaVersion(),
                jpa.getClaveObjeto(),
                jpa.getVersionRepositorioArtefacto(),
                jpa.getNombreDocumento(),
                jpa.getRepositorio(),
                jpa.getNombreRepositorio(),
                jpa.getOmitidoEn(),
                jpa.getOmisionOcurridaEn());
    }
}
