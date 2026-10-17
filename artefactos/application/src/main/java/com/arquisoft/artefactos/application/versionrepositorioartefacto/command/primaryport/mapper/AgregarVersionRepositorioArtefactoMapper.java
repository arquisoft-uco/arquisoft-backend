package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilNumero;

public final class AgregarVersionRepositorioArtefactoMapper {

    private AgregarVersionRepositorioArtefactoMapper() {}

    public static VersionRepositorioArtefactoDomain toDomain(AgregarVersionRepositorioArtefactoCommand command) {
        var archivo = new VersionRepositorioArtefactoDomain.DatosArchivo(
                command.archivo().nombreArchivo(),
                command.archivo().tipoContenido(),
                UtilNumero.obtenerPorDefecto(command.archivo().tamanoBytes()));

        return VersionRepositorioArtefactoDomain.crear(
                command.id(),
                command.nombreDocumento(),
                UtilNumero.obtenerPorDefecto(command.versionDocumento()),
                command.repositorioArtefacto(),
                command.nombreRepositorio(),
                archivo);
    }
}
