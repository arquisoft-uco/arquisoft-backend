package com.arquisoft.artefactos.domain.estudianteartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;

import java.util.UUID;

public final class EstudianteNoVinculadoArtefactoException extends DomainException {

    public EstudianteNoVinculadoArtefactoException(UUID estudiante, UUID artefacto, String nombreEstudiante) {
        super(BaseError.conDetalle(
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_NO_VINCULADO_ARTEFACTO,
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_NO_VINCULADO_ARTEFACTO, nombreEstudiante),
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_NO_VINCULADO_ARTEFACTO_DETALLE, estudiante, artefacto)
        ));
    }
}
