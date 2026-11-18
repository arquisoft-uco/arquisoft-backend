package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioPuntajeEvaluacionCuantitativaAsesorDomain {

    private UUID evaluacionCuantitativaAsesor;
    private UUID asesor;
    private Integer nuevoPuntaje;

    private CambioPuntajeEvaluacionCuantitativaAsesorDomain() {}

    public static CambioPuntajeEvaluacionCuantitativaAsesorDomain crear(
            UUID evaluacionCuantitativaAsesor, UUID asesor, Integer nuevoPuntaje) {
        var cambio = new CambioPuntajeEvaluacionCuantitativaAsesorDomain();
        var result = new ValidationResult();

        cambio.setEvaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor, result);
        cambio.setAsesor(asesor, result);
        cambio.setNuevoPuntaje(nuevoPuntaje, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setEvaluacionCuantitativaAsesor(UUID evaluacionCuantitativaAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ID,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.evaluacionCuantitativaAsesor = evaluacionCuantitativaAsesor;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setNuevoPuntaje(Integer nuevoPuntaje, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(nuevoPuntaje,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(nuevoPuntaje,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MIN,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO, result)) {
            return;
        }
        this.nuevoPuntaje = nuevoPuntaje;
    }

    public UUID getEvaluacionCuantitativaAsesor() {
        return evaluacionCuantitativaAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public Integer getNuevoPuntaje() {
        return nuevoPuntaje;
    }
}
