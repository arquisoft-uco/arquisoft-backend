package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistroEvaluacionesCuantitativasJuradoDomain {

    private UUID evaluacionJurado;
    private UUID jurado;
    private List<EvaluacionCuantitativaJuradoDomain> evaluaciones;

    private RegistroEvaluacionesCuantitativasJuradoDomain() {}

    public static RegistroEvaluacionesCuantitativasJuradoDomain crear(
            UUID evaluacionJurado, UUID jurado, List<EvaluacionCuantitativaJuradoDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCuantitativasJuradoDomain();
        var result = new ValidationResult();

        registro.setEvaluacionJurado(evaluacionJurado, result);
        registro.setJurado(jurado, result);
        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setJurado(UUID jurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result)) {
            return;
        }
        this.jurado = jurado;
    }

    private void setEvaluaciones(List<EvaluacionCuantitativaJuradoDomain> evaluaciones, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(evaluaciones,
                EvaluacionesFields.RegistroEvaluacionesCuantitativasJurado.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO, result)) {
            return;
        }

        var items = evaluaciones.stream().map(EvaluacionCuantitativaJuradoDomain::getItem).toList();
        if (!ValidatorColeccion.sinDuplicados(items,
                EvaluacionesFields.RegistroEvaluacionesCuantitativasJurado.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.ITEMS_REPETIDOS, result)) {
            return;
        }

        this.evaluaciones = List.copyOf(evaluaciones);
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public UUID getJurado() {
        return jurado;
    }

    public List<EvaluacionCuantitativaJuradoDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
