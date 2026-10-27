package com.arquisoft.artefactos.domain.estudiante.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;

import java.util.List;
import java.util.UUID;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException(List<UUID> estudiantes) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Estudiante.NO_ENCONTRADO,
                Mensajes.obtener(EstudianteKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADO_DETALLE, estudiantes)
        ));
    }
}
