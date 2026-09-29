package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoPropietarioException extends DomainException {

    public ProyectoGradoNoPropietarioException(UUID proyectoGrado) {
        super(
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_PROPIETARIO, proyectoGrado),
                MapasRutaCodes.ProyectoGrado.NO_PROPIETARIO
        );
    }
}
