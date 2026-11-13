package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class OmisionEvaluacionesCualitativasAsesorDomain {

    private UUID evaluacionAsesor;
    private UUID asesor;
    private Set<UUID> items;

    private OmisionEvaluacionesCualitativasAsesorDomain() {}

    public static OmisionEvaluacionesCualitativasAsesorDomain crear(
            UUID evaluacionAsesor, UUID asesor, List<UUID> items) {
        var omision = new OmisionEvaluacionesCualitativasAsesorDomain();
        var result = new ValidationResult();

        omision.setEvaluacionAsesor(evaluacionAsesor, result);
        omision.setAsesor(asesor, result);
        omision.setItems(items, result);

        result.lanzarSiTieneErrores();
        return omision;
    }

    private void setEvaluacionAsesor(UUID evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setItems(List<UUID> items, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(items,
                EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.LOTE_VACIO, result)) {
            return;
        }

        if (!ValidatorColeccion.sinDuplicados(items,
                EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.ITEMS_REPETIDOS, result)) {
            return;
        }

        this.items = Set.copyOf(items);
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public Set<UUID> getItems() {
        return items;
    }
}
