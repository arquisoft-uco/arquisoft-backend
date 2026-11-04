package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class VersionArtefactoNoEncontradaException extends DomainException {

    public VersionArtefactoNoEncontradaException(UUID versionArtefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.NO_ENCONTRADA,
                Mensajes.obtener(VersionArtefactoKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(VersionArtefactoKey.ERROR_NO_ENCONTRADA_DETALLE, versionArtefacto)
        ));
    }
}
