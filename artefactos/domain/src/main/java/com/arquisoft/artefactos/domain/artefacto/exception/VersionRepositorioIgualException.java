package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioIgualException extends DomainException {

    public VersionRepositorioIgualException(int version, String documento, UUID id) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_IGUAL_A_ACTUAL,
                Mensajes.formatear(ArtefactoKey.ERROR_VERSION_IGUAL_A_ACTUAL, version, documento),
                Mensajes.formatear(ArtefactoKey.ERROR_VERSION_IGUAL_A_ACTUAL_DETALLE, id)
        ));
    }
}
