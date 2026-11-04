package com.arquisoft.proyectos.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;

import java.util.List;
import java.util.UUID;

public final class EstudianteYaVinculadoException extends DomainException {

    private static final String SEPARADOR_NOMBRES = "», «";

    public EstudianteYaVinculadoException(UUID estudiante, List<String> nombresEstudiantes) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_YA_VINCULADO,
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_YA_VINCULADO,
                        String.join(SEPARADOR_NOMBRES, nombresEstudiantes)),
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_YA_VINCULADO_DETALLE, estudiante)
        ));
    }
}
