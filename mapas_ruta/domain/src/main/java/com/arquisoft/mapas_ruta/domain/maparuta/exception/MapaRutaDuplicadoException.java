package com.arquisoft.mapas_ruta.domain.maparuta.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;

import java.util.UUID;

public final class MapaRutaDuplicadoException extends DomainException {

    public MapaRutaDuplicadoException(UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                MapasRutaCodes.MapaRuta.MAPA_RUTA_DUPLICADO,
                Mensajes.formatear(MapaRutaKey.ERROR_DUPLICADO, tituloProyecto),
                Mensajes.formatear(MapaRutaKey.ERROR_DUPLICADO_DETALLE, proyectoGrado)
        ));
    }
}
