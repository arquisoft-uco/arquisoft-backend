package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistroEvaluacionesCualitativasJuradoDomain {

    private UUID evaluacionJurado;
    private List<EvaluacionCualitativaJuradoDomain> evaluaciones;

    private RegistroEvaluacionesCualitativasJuradoDomain() {}

    public static RegistroEvaluacionesCualitativasJuradoDomain crear(
            UUID evaluacionJurado, List<EvaluacionCualitativaJuradoDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCualitativasJuradoDomain();
        var result = new ValidationResult();

        registro.setEvaluacionJurado(evaluacionJurado, result);
        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setEvaluaciones(List<EvaluacionCualitativaJuradoDomain> evaluaciones, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(evaluaciones,
                EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO, result)) {
            return;
        }

        var items = evaluaciones.stream().map(EvaluacionCualitativaJuradoDomain::getItem).toList();
        if (!ValidatorColeccion.sinDuplicados(items,
                EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.ITEMS_REPETIDOS, result)) {
            return;
        }

        this.evaluaciones = List.copyOf(evaluaciones);
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public List<EvaluacionCualitativaJuradoDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
