package com.arquisoft.mapas_ruta.domain.planestimado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;

import java.util.UUID;

public final class TiempoPlanesExcedePlazoException extends DomainException {

    public TiempoPlanesExcedePlazoException(UUID mapaRuta, long diasAcumulados, int diasNuevoPlan, long plazoDias) {
        super(BaseError.conDetalle(
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_EXCEDE_PLAZO,
                Mensajes.formatear(PlanEstimadoKey.ERROR_TIEMPO_EXCEDE_PLAZO,
                        diasAcumulados + diasNuevoPlan, plazoDias),
                Mensajes.formatear(PlanEstimadoKey.ERROR_TIEMPO_EXCEDE_PLAZO_DETALLE,
                        mapaRuta, diasAcumulados, diasNuevoPlan, plazoDias)
        ));
    }
}
