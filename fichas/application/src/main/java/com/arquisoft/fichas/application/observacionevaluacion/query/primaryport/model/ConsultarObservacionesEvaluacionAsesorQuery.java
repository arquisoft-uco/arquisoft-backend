package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesEvaluacionAsesorQuery(
        UUID evaluacionFichaPerfil,
        UUID asesorFicha
) {

    public static ConsultarObservacionesEvaluacionAsesorQuery crear(
            UUID evaluacionFichaPerfil, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA, result);
        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionEvaluacion.ASESOR_FICHA,
                FichasCodes.ObservacionEvaluacion.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesEvaluacionAsesorQuery(evaluacionFichaPerfil, asesorFicha);
    }
}
