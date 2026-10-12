package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

public final class PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException extends DomainException {

    public PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException(Integer puntaje, Integer valorMaximoItem) {
        super(
                Mensajes.formatear(
                        EvaluacionCuantitativaJuradoKey.ERROR_PUNTAJE_EXCEDE_VALOR_ITEM, puntaje, valorMaximoItem),
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_EXCEDE_VALOR_ITEM
        );
    }
}
