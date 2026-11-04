package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class ArtefactoOmitidoException extends DomainException {

    public ArtefactoOmitidoException(UUID artefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.OMITIDO,
                Mensajes.obtener(ArtefactoKey.ERROR_OMITIDO),
                Mensajes.formatear(ArtefactoKey.ERROR_OMITIDO_DETALLE, artefacto)
        ));
    }
}
