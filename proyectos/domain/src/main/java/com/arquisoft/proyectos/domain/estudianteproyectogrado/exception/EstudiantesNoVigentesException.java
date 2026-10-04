package com.arquisoft.proyectos.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;

import java.util.List;
import java.util.UUID;

public final class EstudiantesNoVigentesException extends DomainException {

    public EstudiantesNoVigentesException(List<UUID> estudiantes) {
        super(
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_ESTUDIANTES_NO_VIGENTES, estudiantes),
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_NO_VIGENTES
        );
    }
}
