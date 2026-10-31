package com.arquisoft.artefactos.infrastructure.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class VersionArtefactoNoDisponibleException extends InfrastructureException {

    public VersionArtefactoNoDisponibleException(UUID versionArtefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.NO_DISPONIBLE,
                Mensajes.obtener(VersionArtefactoKey.ERROR_NO_DISPONIBLE),
                Mensajes.formatear(VersionArtefactoKey.ERROR_NO_DISPONIBLE_DETALLE, versionArtefacto)
        ));
    }
}
