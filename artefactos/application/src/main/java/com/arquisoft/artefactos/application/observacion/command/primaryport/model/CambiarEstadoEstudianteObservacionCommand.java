package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoEstudianteObservacionCommand(
        UUID observacion,
        String estado,
        UUID estudiante
) {

    public static CambiarEstadoEstudianteObservacionCommand crear(UUID observacion, String estado, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacion,
                ArtefactosFields.Observacion.ID,
                ArtefactosCodes.Observacion.ID_REQUERIDO, result);
        var recortado = UtilTexto.aplicarTrim(estado);
        ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.Observacion.ESTADO,
                ArtefactosCodes.Observacion.ESTADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.Observacion.ESTUDIANTE,
                ArtefactosCodes.Observacion.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoEstudianteObservacionCommand(observacion, recortado, estudiante);
    }
}
