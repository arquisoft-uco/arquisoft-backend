package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesEvaluacionRepresentanteQuery(
        UUID evaluacionFichaPerfil,
        UUID representanteComite
) {

    public static ConsultarObservacionesEvaluacionRepresentanteQuery crear(
            UUID evaluacionFichaPerfil, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA, result);
        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesEvaluacionRepresentanteQuery(evaluacionFichaPerfil, representanteComite);
    }
}
