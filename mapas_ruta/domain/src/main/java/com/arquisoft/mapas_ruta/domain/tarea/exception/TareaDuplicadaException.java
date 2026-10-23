package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

import java.util.UUID;

public final class TareaDuplicadaException extends DomainException {

    public TareaDuplicadaException(String descripcion, UUID mapaRuta) {
        super(
                Mensajes.formatear(TareaKey.ERROR_DUPLICADA, descripcion, mapaRuta),
                MapasRutaCodes.Tarea.TAREA_DUPLICADA
        );
    }
}
