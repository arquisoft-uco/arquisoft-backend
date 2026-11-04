package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionCualitativaAsesorDomain {

    public static final EvaluacionCualitativaAsesorDomain VACIO = new EvaluacionCualitativaAsesorDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private UUID evaluacionAsesor;
    private UUID item;
    private UUID criterio;

    private EvaluacionCualitativaAsesorDomain() {}

    private EvaluacionCualitativaAsesorDomain(UUID id, UUID evaluacionAsesor, UUID item, UUID criterio) {
        this.id = id;
        this.evaluacionAsesor = evaluacionAsesor;
        this.item = item;
        this.criterio = criterio;
    }

    public static EvaluacionCualitativaAsesorDomain crear(UUID evaluacionAsesor, UUID item, UUID criterio) {
        var evaluacionCualitativa = new EvaluacionCualitativaAsesorDomain();
        var result = new ValidationResult();

        evaluacionCualitativa.setId();
        evaluacionCualitativa.setEvaluacionAsesor(evaluacionAsesor, result);
        evaluacionCualitativa.setItem(item, result);
        evaluacionCualitativa.setCriterio(criterio, result);

        result.lanzarSiTieneErrores();
        return evaluacionCualitativa;
    }

    public static EvaluacionCualitativaAsesorDomain reconstruir(
            UUID id, UUID evaluacionAsesor, UUID item, UUID criterio) {
        return new EvaluacionCualitativaAsesorDomain(id, evaluacionAsesor, item, criterio);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionAsesor(UUID evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    private void setItem(UUID item, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(item,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ITEM,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO, result)) {
            return;
        }
        this.item = item;
    }

    private void setCriterio(UUID criterio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(criterio,
                EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO, result)) {
            return;
        }
        this.criterio = criterio;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
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
