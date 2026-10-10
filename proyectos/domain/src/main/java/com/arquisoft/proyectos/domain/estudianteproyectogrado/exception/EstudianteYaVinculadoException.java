package com.arquisoft.proyectos.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;

import java.util.UUID;

public final class EstudianteYaVinculadoException extends DomainException {

    public EstudianteYaVinculadoException(UUID estudiante) {
        super(
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_YA_VINCULADO, estudiante),
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_YA_VINCULADO
        );
    }
}
