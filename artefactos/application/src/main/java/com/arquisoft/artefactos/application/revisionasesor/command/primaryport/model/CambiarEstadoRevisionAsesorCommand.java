package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoRevisionAsesorCommand(
        UUID revisionAsesor,
        String estado,
        UUID estudiante
) {

    public static CambiarEstadoRevisionAsesorCommand crear(UUID revisionAsesor, String estado, UUID estudiante) {
        var result = new ValidationResult();
        var estadoRecortado = UtilTexto.aplicarTrim(estado);

        ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.RevisionAsesor.ID,
                ArtefactosCodes.RevisionAsesor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoRecortado,
                ArtefactosFields.RevisionAsesor.ESTADO,
                ArtefactosCodes.RevisionAsesor.ESTADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.RevisionAsesor.ESTUDIANTE,
                ArtefactosCodes.RevisionAsesor.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoRevisionAsesorCommand(revisionAsesor, estadoRecortado, estudiante);
    }
}
