package com.arquisoft.proyectos.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;

import java.util.UUID;

public final class EstudianteNoVinculadoException extends DomainException {

    public EstudianteNoVinculadoException(UUID estudiante, UUID proyectoGrado) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_NO_VINCULADO,
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_NO_VINCULADO),
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_NO_VINCULADO_DETALLE, estudiante, proyectoGrado)
        ));
    }
}
