package com.arquisoft.evaluaciones.domain.evaluacionjurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class JuradosNoEncontradosException extends DomainException {

    public JuradosNoEncontradosException(Set<UUID> jurados) {
        super(
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_JURADOS_NO_ENCONTRADOS, jurados),
                EvaluacionesCodes.EvaluacionJurado.JURADOS_NO_ENCONTRADOS
        );
    }
}
