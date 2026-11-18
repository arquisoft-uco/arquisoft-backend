package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class RemocionJuradosEvaluacionDomain {

    private UUID evaluacion;
    private Set<UUID> jurados;

    private RemocionJuradosEvaluacionDomain() {}

    public static RemocionJuradosEvaluacionDomain crear(UUID evaluacion, List<UUID> jurados) {
        var remocion = new RemocionJuradosEvaluacionDomain();
        var result = new ValidationResult();

        remocion.setEvaluacion(evaluacion, result);
        remocion.setJurados(jurados, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setJurados(List<UUID> jurados, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(jurados,
                EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO, result)) {
            return;
        }
        if (!ValidatorColeccion.sinDuplicados(jurados,
                EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                EvaluacionesCodes.AsignacionJuradosEvaluacion.JURADOS_REPETIDOS, result)) {
            return;
        }
        this.jurados = Set.copyOf(jurados);
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public Set<UUID> getJurados() {
        return jurados;
    }
}
