package com.arquisoft.mapas_ruta.domain.planreal.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;

import java.util.UUID;

public final class PlanRealNoEncontradoException extends DomainException {

    public PlanRealNoEncontradoException(UUID planReal) {
        super(BaseError.conDetalle(
                MapasRutaCodes.PlanReal.PLAN_REAL_NO_ENCONTRADO,
                Mensajes.formatear(PlanRealKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(PlanRealKey.ERROR_NO_ENCONTRADO_DETALLE, planReal)
        ));
    }
}
