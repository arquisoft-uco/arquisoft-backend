package com.arquisoft.artefactos.domain.estudianteartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;

import java.util.UUID;

public final class EstudianteNoVinculadoProyectoGradoException extends DomainException {

    public EstudianteNoVinculadoProyectoGradoException(UUID estudiante, String nombreEstudiante) {
        super(BaseError.conDetalle(
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_NO_VINCULADO_PROYECTO_GRADO,
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_NO_VINCULADO_PROYECTO_GRADO, nombreEstudiante),
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_NO_VINCULADO_PROYECTO_GRADO_DETALLE, estudiante)
        ));
    }
}
