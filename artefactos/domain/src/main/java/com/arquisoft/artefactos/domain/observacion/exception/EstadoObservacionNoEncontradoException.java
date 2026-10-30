package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

public final class EstadoObservacionNoEncontradoException extends DomainException {

    public EstadoObservacionNoEncontradoException(String id) {
        super(
                Mensajes.formatear(ObservacionKey.ERROR_ESTADO_INVALIDO, id),
                ArtefactosCodes.Observacion.ESTADO_INVALIDO
        );
    }
}
