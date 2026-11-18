package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioCriterioEvaluacionCualitativaAsesorDomain {

    private UUID evaluacionCualitativaAsesor;
    private UUID asesor;
    private UUID criterio;

    private CambioCriterioEvaluacionCualitativaAsesorDomain() {}

    public static CambioCriterioEvaluacionCualitativaAsesorDomain crear(
            UUID evaluacionCualitativaAsesor, UUID asesor, UUID criterio) {
        var cambio = new CambioCriterioEvaluacionCualitativaAsesorDomain();
        var result = new ValidationResult();

        cambio.setEvaluacionCualitativaAsesor(evaluacionCualitativaAsesor, result);
        cambio.setAsesor(asesor, result);
        cambio.setCriterio(criterio, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setEvaluacionCualitativaAsesor(UUID evaluacionCualitativaAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCualitativaAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_REQUERIDA, result)) {
            return;
        }
        this.evaluacionCualitativaAsesor = evaluacionCualitativaAsesor;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setCriterio(UUID criterio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(criterio,
                EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO, result)) {
            return;
        }
        this.criterio = criterio;
    }

    public UUID getEvaluacionCualitativaAsesor() {
        return evaluacionCualitativaAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getCriterio() {
        return criterio;
    }
}
