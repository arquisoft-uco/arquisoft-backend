package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

import java.util.UUID;

public final class TareaDuplicadaException extends DomainException {

    public TareaDuplicadaException(String descripcion, UUID mapaRuta) {
        super(BaseError.conDetalle(
                MapasRutaCodes.Tarea.TAREA_DUPLICADA,
                Mensajes.formatear(TareaKey.ERROR_DUPLICADA, descripcion),
                Mensajes.formatear(TareaKey.ERROR_DUPLICADA_DETALLE, descripcion, mapaRuta)
        ));
    }
}
