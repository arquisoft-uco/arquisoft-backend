package com.arquisoft.artefactos.domain.estudiante.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;

import java.util.List;
import java.util.UUID;

public final class EstudiantesNoEncontradosException extends DomainException {

    public EstudiantesNoEncontradosException(List<UUID> estudiantes) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Estudiante.NO_ENCONTRADOS,
                Mensajes.obtener(EstudianteKey.ERROR_NO_ENCONTRADOS),
                Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADOS_DETALLE, estudiantes)
        ));
    }
}
