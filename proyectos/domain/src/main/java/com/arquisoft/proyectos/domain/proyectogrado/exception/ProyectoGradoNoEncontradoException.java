package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoEncontradoException extends DomainException {

    public ProyectoGradoNoEncontradoException(UUID proyectoGrado) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.NO_ENCONTRADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_ENCONTRADO_DETALLE, proyectoGrado)
        ));
    }
}
