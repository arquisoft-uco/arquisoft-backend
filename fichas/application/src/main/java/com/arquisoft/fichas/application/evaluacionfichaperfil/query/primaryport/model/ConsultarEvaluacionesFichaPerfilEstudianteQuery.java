package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesFichaPerfilEstudianteQuery(
        UUID fichaPerfil,
        UUID estudiante
) {

    public static ConsultarEvaluacionesFichaPerfilEstudianteQuery crear(UUID fichaPerfil, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA, result);

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.EvaluacionFichaPerfil.ESTUDIANTE,
                FichasCodes.EvaluacionFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesFichaPerfilEstudianteQuery(fichaPerfil, estudiante);
    }
}
