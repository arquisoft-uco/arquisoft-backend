package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class ObservacionNoReabribleException extends DomainException {

    public ObservacionNoReabribleException(
            UUID observacion, int numeroVersion, int numeroUltimaVersion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.NO_REABRIBLE,
                Mensajes.obtener(ObservacionKey.ERROR_NO_REABRIBLE),
                Mensajes.formatear(ObservacionKey.ERROR_NO_REABRIBLE_DETALLE,
                        observacion, numeroVersion, numeroUltimaVersion)
        ));
    }
}
