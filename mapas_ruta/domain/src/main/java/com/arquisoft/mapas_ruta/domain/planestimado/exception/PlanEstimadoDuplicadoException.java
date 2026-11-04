package com.arquisoft.mapas_ruta.domain.planestimado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;

import java.util.UUID;

public final class PlanEstimadoDuplicadoException extends DomainException {

    public PlanEstimadoDuplicadoException(String descripcionTarea, UUID tarea) {
        super(BaseError.conDetalle(
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DUPLICADO,
                Mensajes.formatear(PlanEstimadoKey.ERROR_DUPLICADO, descripcionTarea),
                Mensajes.formatear(PlanEstimadoKey.ERROR_DUPLICADO_DETALLE, tarea)
        ));
    }
}
