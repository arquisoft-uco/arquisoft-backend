package com.arquisoft.mapas_ruta.domain.maparuta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;

import java.time.LocalDate;

public final class FechaFinMapaRutaNoPosteriorException extends DomainException {

    public FechaFinMapaRutaNoPosteriorException(LocalDate fechaFin, LocalDate fechaInicio) {
        super(
                Mensajes.formatear(MapaRutaKey.ERROR_FECHA_FIN_NO_POSTERIOR, fechaFin, fechaInicio),
                MapasRutaCodes.MapaRuta.FECHA_FIN_NO_POSTERIOR
        );
    }
}
