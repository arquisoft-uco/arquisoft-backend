package com.arquisoft.evaluaciones.domain.observacionitemjurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;

import java.util.UUID;

public final class ObservacionItemJuradoNoEncontradaException extends DomainException {

    public ObservacionItemJuradoNoEncontradaException(UUID observacionItemJurado) {
        super(
                Mensajes.formatear(
                        ObservacionItemJuradoKey.ERROR_OBSERVACION_NO_ENCONTRADA,
                        observacionItemJurado),
                EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA
        );
    }
}
