package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class ArtefactoNoEncontradoException extends DomainException {

    public ArtefactoNoEncontradoException(UUID artefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.NO_ENCONTRADO,
                Mensajes.obtener(ArtefactoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ArtefactoKey.ERROR_NO_ENCONTRADO_DETALLE, artefacto)
        ));
    }
}
