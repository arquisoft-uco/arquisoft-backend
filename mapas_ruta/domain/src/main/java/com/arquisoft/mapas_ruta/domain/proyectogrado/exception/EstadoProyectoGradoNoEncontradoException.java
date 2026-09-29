package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoKey;

public final class EstadoProyectoGradoNoEncontradoException extends DomainException {

    public EstadoProyectoGradoNoEncontradoException(String estado) {
        super(
                Mensajes.formatear(ProyectoGradoKey.ERROR_ESTADO_NO_ENCONTRADO, estado),
                MapasRutaCodes.ProyectoGrado.ESTADO_NO_ENCONTRADO
        );
    }
}
