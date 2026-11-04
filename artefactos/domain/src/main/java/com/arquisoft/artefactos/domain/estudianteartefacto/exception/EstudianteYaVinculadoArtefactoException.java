package com.arquisoft.artefactos.domain.estudianteartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;

import java.util.UUID;

public final class EstudianteYaVinculadoArtefactoException extends DomainException {

    public EstudianteYaVinculadoArtefactoException(UUID estudiante, String nombreEstudiante) {
        super(BaseError.conDetalle(
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_YA_VINCULADO,
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_YA_VINCULADO, nombreEstudiante),
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_YA_VINCULADO_DETALLE, estudiante)
        ));
    }
}
