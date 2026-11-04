package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AsignacionJuradosEvaluacionDomain {

    private UUID evaluacion;
    private List<EvaluacionJuradoDomain> juradosEvaluacion;

    private AsignacionJuradosEvaluacionDomain() {}

    public static AsignacionJuradosEvaluacionDomain crear(
            UUID evaluacion, List<EvaluacionJuradoDomain> juradosEvaluacion) {
        var asignacion = new AsignacionJuradosEvaluacionDomain();
        var result = new ValidationResult();

        asignacion.setEvaluacion(evaluacion, result);
        asignacion.setJuradosEvaluacion(juradosEvaluacion, result);

        result.lanzarSiTieneErrores();
        return asignacion;
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setJuradosEvaluacion(List<EvaluacionJuradoDomain> juradosEvaluacion, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(juradosEvaluacion,
                EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO, result)) {
            return;
        }
        var jurados = juradosEvaluacion.stream().map(EvaluacionJuradoDomain::getJurado).toList();
        if (!ValidatorColeccion.sinDuplicados(jurados,
                EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                EvaluacionesCodes.AsignacionJuradosEvaluacion.JURADOS_REPETIDOS, result)) {
            return;
        }
        this.juradosEvaluacion = List.copyOf(juradosEvaluacion);
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public List<EvaluacionJuradoDomain> getJuradosEvaluacion() {
        return juradosEvaluacion;
    }

    public Set<UUID> getJurados() {
        return juradosEvaluacion.stream()
                .map(EvaluacionJuradoDomain::getJurado)
                .collect(Collectors.toUnmodifiableSet());
    }
}
