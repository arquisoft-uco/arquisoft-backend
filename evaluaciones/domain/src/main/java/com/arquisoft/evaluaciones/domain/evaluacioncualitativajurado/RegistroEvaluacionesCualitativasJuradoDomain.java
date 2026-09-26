package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;

import java.util.List;

public final class RegistroEvaluacionesCualitativasJuradoDomain {

    private List<EvaluacionCualitativaJuradoDomain> evaluaciones;

    private RegistroEvaluacionesCualitativasJuradoDomain() {}

    public static RegistroEvaluacionesCualitativasJuradoDomain crear(
            List<EvaluacionCualitativaJuradoDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCualitativasJuradoDomain();
        var result = new ValidationResult();

        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
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

        long padresDistintos = evaluaciones.stream()
                .map(EvaluacionCualitativaJuradoDomain::getEvaluacionJurado)
                .distinct()
                .count();
        if (padresDistintos > 1) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.PADRES_DISTINTOS,
                    Mensajes.formatear(EvaluacionCualitativaJuradoKey.ERROR_PADRES_DISTINTOS));
            return;
        }

        this.evaluaciones = List.copyOf(evaluaciones);
    }

    public List<EvaluacionCualitativaJuradoDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
