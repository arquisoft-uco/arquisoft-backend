package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;

public final class SolicitudCargaMapper {

    private SolicitudCargaMapper() {}

    public static SolicitudCargaEntity toEntity(SolicitudCargaVersionRepositorioArtefactoDomain solicitud) {
        return new SolicitudCargaEntity(
                solicitud.getCarga(),
                solicitud.getRepositorio(),
                solicitud.getSolicitante(),
                solicitud.getNombreArchivo(),
                solicitud.getTipoContenido(),
                solicitud.getTamanoBytes());
    }
}
