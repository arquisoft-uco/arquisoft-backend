package com.arquisoft.evaluaciones.domain.evaluacionjurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class JuradosEvaluadosException extends DomainException {

    public JuradosEvaluadosException(Set<UUID> jurados) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionJurado.JURADOS_EVALUADOS,
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_JURADOS_EVALUADOS),
                Mensajes.formatear(EvaluacionJuradoKey.ERROR_JURADOS_EVALUADOS_DETALLE, jurados)
        ));
    }
}
