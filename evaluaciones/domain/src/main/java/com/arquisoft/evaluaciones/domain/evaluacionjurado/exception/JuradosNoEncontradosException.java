package com.arquisoft.evaluaciones.domain.evaluacionjurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class JuradosNoEncontradosException extends DomainException {

    public JuradosNoEncontradosException(Set<UUID> jurados) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionJurado.JURADOS_NO_ENCONTRADOS,
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_JURADOS_NO_ENCONTRADOS),
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_JURADOS_NO_ENCONTRADOS_DETALLE, jurados)
        ));
    }
}
