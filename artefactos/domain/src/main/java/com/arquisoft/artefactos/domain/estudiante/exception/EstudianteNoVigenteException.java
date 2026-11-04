package com.arquisoft.artefactos.domain.estudiante.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;

import java.util.UUID;

public final class EstudianteNoVigenteException extends DomainException {

    public EstudianteNoVigenteException(UUID estudiante, String nombreEstudiante) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Estudiante.NO_VIGENTE,
                Mensajes.formatear(EstudianteKey.ERROR_NO_VIGENTE, nombreEstudiante),
                Mensajes.formatear(EstudianteKey.ERROR_NO_VIGENTE_DETALLE, estudiante)
        ));
    }
}
