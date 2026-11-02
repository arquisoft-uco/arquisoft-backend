package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

import java.util.UUID;

public final class ArchivoVersionNoDisponibleException extends InfrastructureException {

    public ArchivoVersionNoDisponibleException(UUID version, String key) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.ARCHIVO_NO_DISPONIBLE,
                Mensajes.obtener(VersionRepositorioArtefactoKey.ERROR_ARCHIVO_NO_DISPONIBLE),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE, version, key)
        ));
    }
}
