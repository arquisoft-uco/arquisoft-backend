package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

import java.util.UUID;

public final class TransicionEstadoItemNoPermitidaException extends DomainException {

    public TransicionEstadoItemNoPermitidaException(UUID tarea, EstadoItem actual, EstadoItem nuevo) {
        super(
                Mensajes.formatear(TareaKey.ERROR_TRANSICION_ESTADO_ITEM_NO_PERMITIDA,
                        tarea, actual.getNombre(), nuevo.getNombre()),
                MapasRutaCodes.Tarea.TRANSICION_ESTADO_ITEM_NO_PERMITIDA
        );
    }
}
