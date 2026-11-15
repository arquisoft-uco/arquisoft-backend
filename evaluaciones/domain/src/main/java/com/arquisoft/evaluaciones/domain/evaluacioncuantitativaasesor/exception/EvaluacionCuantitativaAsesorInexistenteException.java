package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;

import java.util.UUID;

public final class EvaluacionCuantitativaAsesorInexistenteException extends DomainException {

    public EvaluacionCuantitativaAsesorInexistenteException(UUID evaluacionCuantitativaAsesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.NO_ENCONTRADA,
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_NO_ENCONTRADA_DETALLE, evaluacionCuantitativaAsesor)
        ));
    }
}
