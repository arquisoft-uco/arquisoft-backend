package com.arquisoft.mapas_ruta.domain.maparuta.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;

import java.util.UUID;

public final class MapaRutaNoEncontradoException extends DomainException {

    public MapaRutaNoEncontradoException(UUID mapaRuta) {
        super(BaseError.conDetalle(
                MapasRutaCodes.MapaRuta.MAPA_RUTA_NO_ENCONTRADO,
                Mensajes.formatear(MapaRutaKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(MapaRutaKey.ERROR_NO_ENCONTRADO_DETALLE, mapaRuta)
        ));
    }
}
