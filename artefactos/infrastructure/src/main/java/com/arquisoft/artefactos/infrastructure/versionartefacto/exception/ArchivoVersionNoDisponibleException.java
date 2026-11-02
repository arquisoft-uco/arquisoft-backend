package com.arquisoft.artefactos.infrastructure.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class ArchivoVersionNoDisponibleException extends InfrastructureException {

    public ArchivoVersionNoDisponibleException(UUID versionArtefacto, String key) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.ARCHIVO_NO_DISPONIBLE,
                Mensajes.obtener(VersionArtefactoKey.ERROR_ARCHIVO_NO_DISPONIBLE),
                Mensajes.formatear(VersionArtefactoKey.ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE, versionArtefacto, key)
        ));
    }
}
