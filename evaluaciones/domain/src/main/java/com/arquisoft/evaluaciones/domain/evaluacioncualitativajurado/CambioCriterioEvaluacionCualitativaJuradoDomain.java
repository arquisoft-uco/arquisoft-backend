package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioCriterioEvaluacionCualitativaJuradoDomain {

    private UUID evaluacionCualitativaJurado;
    private UUID jurado;
    private UUID criterio;

    private CambioCriterioEvaluacionCualitativaJuradoDomain() {}

    public static CambioCriterioEvaluacionCualitativaJuradoDomain crear(
            UUID evaluacionCualitativaJurado, UUID jurado, UUID criterio) {
        var cambio = new CambioCriterioEvaluacionCualitativaJuradoDomain();
        var result = new ValidationResult();

        cambio.setEvaluacionCualitativaJurado(evaluacionCualitativaJurado, result);
        cambio.setJurado(jurado, result);
        cambio.setCriterio(criterio, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setEvaluacionCualitativaJurado(UUID evaluacionCualitativaJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCualitativaJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.ID,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ID_REQUERIDO, result)) {
            return;
        }
        this.evaluacionCualitativaJurado = evaluacionCualitativaJurado;
    }

    private void setJurado(UUID jurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO, result)) {
            return;
        }
        this.jurado = jurado;
    }

    private void setCriterio(UUID criterio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(criterio,
                EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO, result)) {
            return;
        }
        this.criterio = criterio;
    }

    public UUID getEvaluacionCualitativaJurado() {
        return evaluacionCualitativaJurado;
    }

    public UUID getJurado() {
        return jurado;
    }

    public UUID getCriterio() {
        return criterio;
    }
}
