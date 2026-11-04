package com.arquisoft.mapas_ruta.domain.planestimado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;

import java.util.UUID;

public final class PlanEstimadoNoEncontradoException extends DomainException {

    public PlanEstimadoNoEncontradoException(UUID planEstimado) {
        super(BaseError.conDetalle(
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_NO_ENCONTRADO,
                Mensajes.formatear(PlanEstimadoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(PlanEstimadoKey.ERROR_NO_ENCONTRADO_DETALLE, planEstimado)
        ));
    }
}
