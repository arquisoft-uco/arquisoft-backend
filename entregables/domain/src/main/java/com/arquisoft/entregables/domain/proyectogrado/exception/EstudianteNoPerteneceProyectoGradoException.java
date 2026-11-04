package com.arquisoft.entregables.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;

import java.util.UUID;

public final class EstudianteNoPerteneceProyectoGradoException extends DomainException {

    public EstudianteNoPerteneceProyectoGradoException(UUID proyectoGrado, UUID estudiante) {
        super(BaseError.conDetalle(
                EntregablesCodes.ProyectoGrado.ESTUDIANTE_NO_PERTENECE,
                Mensajes.formatear(ProyectoGradoKey.ERROR_ESTUDIANTE_NO_PERTENECE),
                Mensajes.formatear(ProyectoGradoKey.ERROR_ESTUDIANTE_NO_PERTENECE_DETALLE, estudiante, proyectoGrado)
        ));
    }
}
