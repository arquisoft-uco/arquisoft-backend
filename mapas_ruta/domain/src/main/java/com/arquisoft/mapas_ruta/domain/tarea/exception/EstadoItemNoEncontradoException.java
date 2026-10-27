package com.arquisoft.mapas_ruta.domain.tarea.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;

public final class EstadoItemNoEncontradoException extends DomainException {

    public EstadoItemNoEncontradoException(String estado) {
        super(BaseError.conDetalle(
                MapasRutaCodes.Tarea.ESTADO_ITEM_NO_ENCONTRADO,
                Mensajes.formatear(TareaKey.ERROR_ESTADO_ITEM_NO_ENCONTRADO),
                Mensajes.formatear(TareaKey.ERROR_ESTADO_ITEM_NO_ENCONTRADO_DETALLE, estado)
        ));
    }
}
