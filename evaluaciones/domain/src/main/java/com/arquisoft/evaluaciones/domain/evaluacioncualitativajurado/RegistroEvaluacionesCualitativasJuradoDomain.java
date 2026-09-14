package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistroEvaluacionesCualitativasJuradoDomain {

    private UUID actor;
    private List<EvaluacionCualitativaJuradoDomain> evaluaciones;

    private RegistroEvaluacionesCualitativasJuradoDomain() {}

    public static RegistroEvaluacionesCualitativasJuradoDomain crear(
            UUID actor, List<EvaluacionCualitativaJuradoDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCualitativasJuradoDomain();
        var result = new ValidationResult();

        registro.setActor(actor, result);
        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setActor(UUID actor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(actor,
                EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.ACTOR,
                EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.ACTOR_REQUERIDO, result)) {
            return;
        }
        this.actor = actor;
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

    public UUID getActor() {
        return actor;
    }

    public List<EvaluacionCualitativaJuradoDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
