package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;

public final class EstadoProyectoGradoNoEncontradoException extends DomainException {

    public EstadoProyectoGradoNoEncontradoException(String estado) {
        super(BaseError.conDetalle(
                MapasRutaCodes.ProyectoGrado.ESTADO_NO_ENCONTRADO,
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_ESTADO_NO_ENCONTRADO),
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_ESTADO_NO_ENCONTRADO_DETALLE, estado)
        ));
    }
}
