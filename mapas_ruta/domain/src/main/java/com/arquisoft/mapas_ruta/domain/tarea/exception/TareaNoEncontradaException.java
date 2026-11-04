package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

import java.util.UUID;

public final class TareaNoEncontradaException extends DomainException {

    public TareaNoEncontradaException(UUID tarea) {
        super(
                Mensajes.formatear(TareaKey.ERROR_NO_ENCONTRADA, tarea),
                MapasRutaCodes.Tarea.TAREA_NO_ENCONTRADA
        );
    }
}
