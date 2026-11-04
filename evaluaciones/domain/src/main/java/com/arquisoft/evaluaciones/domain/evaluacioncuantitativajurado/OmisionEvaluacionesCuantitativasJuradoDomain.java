package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class OmisionEvaluacionesCuantitativasJuradoDomain {

    private UUID evaluacionJurado;
    private Set<UUID> evaluaciones;

    private OmisionEvaluacionesCuantitativasJuradoDomain() {}

    public static OmisionEvaluacionesCuantitativasJuradoDomain crear(UUID evaluacionJurado, List<UUID> evaluaciones) {
        var omision = new OmisionEvaluacionesCuantitativasJuradoDomain();
        var result = new ValidationResult();

        omision.setEvaluacionJurado(evaluacionJurado, result);
        omision.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return omision;
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setEvaluaciones(List<UUID> evaluaciones, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(evaluaciones,
                EvaluacionesFields.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES,
                EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado.LOTE_VACIO, result)) {
            return;
        }

        if (!ValidatorColeccion.sinDuplicados(evaluaciones,
                EvaluacionesFields.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES,
                EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES_REPETIDAS, result)) {
            return;
        }

        this.evaluaciones = Set.copyOf(evaluaciones);
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public Set<UUID> getEvaluaciones() {
        return evaluaciones;
    }
}
