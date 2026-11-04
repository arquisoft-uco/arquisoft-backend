package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

import java.util.UUID;

public final class TareaNoRemovibleException extends DomainException {

    public TareaNoRemovibleException(UUID tarea, EstadoItem estadoItem) {
        super(
                Mensajes.formatear(TareaKey.ERROR_NO_REMOVIBLE, tarea, estadoItem.getNombre()),
                MapasRutaCodes.Tarea.TAREA_NO_REMOVIBLE
        );
    }
}
