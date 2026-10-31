package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class ObservacionNoEncontradaException extends DomainException {

    public ObservacionNoEncontradaException(UUID observacion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.NO_ENCONTRADA,
                Mensajes.obtener(ObservacionKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(ObservacionKey.ERROR_NO_ENCONTRADA_DETALLE, observacion)
        ));
    }
}
