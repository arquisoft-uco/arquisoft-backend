package com.arquisoft.artefactos.domain.estudianteartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;

public final class EstudiantesSuperanProyectoException extends DomainException {

    public EstudiantesSuperanProyectoException(int maximo, int total) {
        super(BaseError.conDetalle(
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_SUPERAN_PROYECTO,
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_SUPERAN_PROYECTO, maximo),
                Mensajes.formatear(EstudianteArtefactoKey.ERROR_SUPERAN_PROYECTO_DETALLE, total, maximo)
        ));
    }
}
