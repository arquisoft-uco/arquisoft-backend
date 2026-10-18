package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionJuradoDomain {

    private UUID id;
    private UUID evaluacion;
    private UUID jurado;

    private EvaluacionJuradoDomain() {}

    private EvaluacionJuradoDomain(UUID id, UUID evaluacion, UUID jurado) {
        this.id = id;
        this.evaluacion = evaluacion;
        this.jurado = jurado;
    }

    public static EvaluacionJuradoDomain crear(UUID evaluacion, UUID jurado) {
        var evaluacionJurado = new EvaluacionJuradoDomain();
        var result = new ValidationResult();

        evaluacionJurado.setId();
        evaluacionJurado.setEvaluacion(evaluacion, result);
        evaluacionJurado.setJurado(jurado, result);

        result.lanzarSiTieneErrores();
        return evaluacionJurado;
    }

    public static EvaluacionJuradoDomain reconstruir(UUID id, UUID evaluacion, UUID jurado) {
        return new EvaluacionJuradoDomain(id, evaluacion, jurado);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setJurado(UUID jurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionJurado.JURADO,
                EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO, result)) {
            return;
        }
        this.jurado = jurado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public UUID getJurado() {
        return jurado;
    }
}
