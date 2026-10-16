package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SolicitarCargaVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;

public final class SolicitarCargaVersionRepositorioArtefactoMapper {

    private SolicitarCargaVersionRepositorioArtefactoMapper() {}

    public static SolicitudCargaVersionRepositorioArtefactoDomain toDomain(
            SolicitarCargaVersionRepositorioArtefactoCommand command) {
        return SolicitudCargaVersionRepositorioArtefactoDomain.crear(
                command.repositorio(),
                command.solicitante(),
                command.nombreArchivo(),
                command.tipoContenido(),
                command.tamanoBytes());
    }
}
