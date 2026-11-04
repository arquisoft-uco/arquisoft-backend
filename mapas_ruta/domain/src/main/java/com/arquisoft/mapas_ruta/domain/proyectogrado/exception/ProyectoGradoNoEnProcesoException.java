package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;

import java.util.UUID;

public final class ProyectoGradoNoEnProcesoException extends DomainException {

    public ProyectoGradoNoEnProcesoException(UUID proyectoGrado, String tituloProyecto, String estado) {
        super(BaseError.conDetalle(
                MapasRutaCodes.ProyectoGrado.NO_EN_PROCESO,
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_NO_EN_PROCESO, tituloProyecto, estado),
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_NO_EN_PROCESO_DETALLE, proyectoGrado, estado)
        ));
    }
}
