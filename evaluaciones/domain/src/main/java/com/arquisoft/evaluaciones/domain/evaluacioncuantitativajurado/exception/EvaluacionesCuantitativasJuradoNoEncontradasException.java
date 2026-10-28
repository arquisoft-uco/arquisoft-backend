package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCuantitativasJuradoNoEncontradasException extends DomainException {

    public EvaluacionesCuantitativasJuradoNoEncontradasException(Set<UUID> evaluaciones, UUID evaluacionJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACIONES_NO_ENCONTRADAS,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_EVALUACIONES_NO_ENCONTRADAS),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_EVALUACIONES_NO_ENCONTRADAS_DETALLE, evaluaciones, evaluacionJurado)
        ));
    }
}
