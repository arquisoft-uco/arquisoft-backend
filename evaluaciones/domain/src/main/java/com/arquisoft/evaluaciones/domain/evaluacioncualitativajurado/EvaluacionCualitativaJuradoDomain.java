package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionCualitativaJuradoDomain {

    public static final EvaluacionCualitativaJuradoDomain VACIO = new EvaluacionCualitativaJuradoDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private UUID evaluacionJurado;
    private UUID item;
    private UUID criterio;

    private EvaluacionCualitativaJuradoDomain() {}

    private EvaluacionCualitativaJuradoDomain(UUID id, UUID evaluacionJurado, UUID item, UUID criterio) {
        this.id = id;
        this.evaluacionJurado = evaluacionJurado;
        this.item = item;
        this.criterio = criterio;
    }

    public static EvaluacionCualitativaJuradoDomain crear(UUID evaluacionJurado, UUID item, UUID criterio) {
        var evaluacionCualitativa = new EvaluacionCualitativaJuradoDomain();
        var result = new ValidationResult();

        evaluacionCualitativa.setId();
        evaluacionCualitativa.setEvaluacionJurado(evaluacionJurado, result);
        evaluacionCualitativa.setItem(item, result);
        evaluacionCualitativa.setCriterio(criterio, result);

        result.lanzarSiTieneErrores();
        return evaluacionCualitativa;
    }

    public static EvaluacionCualitativaJuradoDomain reconstruir(
            UUID id, UUID evaluacionJurado, UUID item, UUID criterio) {
        return new EvaluacionCualitativaJuradoDomain(id, evaluacionJurado, item, criterio);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionJurado(UUID evaluacionJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result)) {
            return;
        }
        this.evaluacionJurado = evaluacionJurado;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                EvaluacionesFields.EvaluacionCualitativaJurado.ITEM,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setCriterio(UUID criterio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(criterio,
                EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO, result)) {
            return;
        }
        this.criterio = criterio;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionJurado() {
        return evaluacionJurado;
    }

    public UUID getItem() {
        return item;
    }

    public UUID getCriterio() {
        return criterio;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
