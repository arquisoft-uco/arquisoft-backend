package com.arquisoft.entregables.application.artefacto.command.secondaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;

public final class ArtefactoMapper {

    private ArtefactoMapper() {}

    public static ArtefactoEntity toEntity(ArtefactoDomain artefacto) {
        return new ArtefactoEntity(
                artefacto.getId(),
                artefacto.getProyectoGrado(),
                artefacto.getVersionArtefacto(),
                artefacto.getNumeroVersion(),
                artefacto.getFechaVersion(),
                artefacto.getClaveObjeto(),
                artefacto.getVersionRepositorioArtefacto(),
                artefacto.getNombreDocumento(),
                artefacto.getRepositorio(),
                artefacto.getNombreRepositorio(),
                artefacto.getOmitidoEn(),
                artefacto.getOmisionOcurridaEn());
    }

    public static ArtefactoDomain toDomain(ArtefactoEntity entity) {
        return ArtefactoDomain.reconstruir(
                entity.id(),
                entity.proyectoGrado(),
                entity.nombreDocumento(),
                entity.claveObjeto(),
                new ArtefactoDomain.VersionCerrada(
                        entity.versionArtefacto(), entity.numeroVersion(), entity.fechaVersion()),
                new ArtefactoDomain.DatosRepositorio(
                        entity.versionRepositorioArtefacto(), entity.repositorio(), entity.nombreRepositorio()),
                new ArtefactoDomain.Omision(entity.omitidoEn(), entity.omisionOcurridaEn()));
    }
}
