package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

public final class PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException extends DomainException {

    public PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException(
            Integer puntaje, Integer valorMaximoItem, String nombreItem) {
        super(
                Mensajes.formatear(
                        EvaluacionCuantitativaAsesorKey.ERROR_PUNTAJE_EXCEDE_VALOR_ITEM,
                        puntaje, valorMaximoItem, nombreItem),
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_EXCEDE_VALOR_ITEM
        );
    }
}
