package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;

import java.util.UUID;

public final class ProyectoGradoNoPropietarioException extends DomainException {

    public ProyectoGradoNoPropietarioException(UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                MapasRutaCodes.ProyectoGrado.NO_PROPIETARIO,
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_NO_PROPIETARIO, tituloProyecto),
                Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_NO_PROPIETARIO_DETALLE, proyectoGrado)
        ));
    }
}
