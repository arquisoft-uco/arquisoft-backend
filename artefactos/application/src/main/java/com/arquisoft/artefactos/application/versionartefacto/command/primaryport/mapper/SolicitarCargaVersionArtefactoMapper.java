package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.SolicitarCargaVersionArtefactoCommand;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;

public final class SolicitarCargaVersionArtefactoMapper {

    private SolicitarCargaVersionArtefactoMapper() {}

    public static SolicitudCargaVersionArtefactoDomain toDomain(SolicitarCargaVersionArtefactoCommand command) {
        return SolicitudCargaVersionArtefactoDomain.crear(
                command.artefacto(),
                command.solicitante(),
                command.nombreArchivo(),
                command.tipoContenido(),
                command.tamanoBytes());
    }
}
