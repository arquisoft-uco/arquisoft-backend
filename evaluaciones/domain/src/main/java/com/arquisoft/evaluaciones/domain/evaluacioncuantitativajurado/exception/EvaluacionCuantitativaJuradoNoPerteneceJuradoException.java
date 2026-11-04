package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.UUID;

public final class EvaluacionCuantitativaJuradoNoPerteneceJuradoException extends DomainException {

    public EvaluacionCuantitativaJuradoNoPerteneceJuradoException(UUID evaluacionCuantitativaJurado) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.NO_PERTENECE_JURADO,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_NO_PERTENECE_JURADO),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_NO_PERTENECE_JURADO_DETALLE, evaluacionCuantitativaJurado)
        ));
    }
}
