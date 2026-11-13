package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;

import java.util.Set;
import java.util.UUID;

public final class EvaluacionesCuantitativasJuradoDuplicadasException extends DomainException {

    public EvaluacionesCuantitativasJuradoDuplicadasException(Set<UUID> ids) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEMS_YA_REGISTRADOS,
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_ITEMS_YA_REGISTRADOS),
                Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_ITEMS_YA_REGISTRADOS_DETALLE, ids)
        ));
    }
}
