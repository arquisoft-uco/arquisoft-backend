package com.arquisoft.mapas_ruta.domain.maparuta.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;

import java.util.UUID;

public final class MapaRutaDuplicadoException extends DomainException {

    public MapaRutaDuplicadoException(UUID proyectoGrado) {
        super(
                Mensajes.formatear(MapaRutaKey.ERROR_DUPLICADO, proyectoGrado),
                MapasRutaCodes.MapaRuta.MAPA_RUTA_DUPLICADO
        );
    }
}
