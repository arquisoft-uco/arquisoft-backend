package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;

public final class SolicitudCargaMapper {

    private SolicitudCargaMapper() {}

    public static SolicitudCargaEntity toEntity(SolicitudCargaVersionArtefactoDomain solicitud) {
        return new SolicitudCargaEntity(
                solicitud.getCarga(),
                solicitud.getArtefacto(),
                solicitud.getSolicitante(),
                solicitud.getNombreArchivo(),
                solicitud.getTipoContenido(),
                solicitud.getTamanoBytes());
    }
}
