package com.arquisoft.mapas_ruta.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoEncontradoException extends DomainException {

    public ProyectoGradoNoEncontradoException(UUID proyectoGrado) {
        super(
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_ENCONTRADO, proyectoGrado),
                MapasRutaCodes.ProyectoGrado.NO_ENCONTRADO
        );
    }
}
