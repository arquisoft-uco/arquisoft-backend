package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistroEvaluacionesCualitativasAsesorDomain {

    private UUID evaluacionAsesor;
    private UUID asesor;
    private List<EvaluacionCualitativaAsesorDomain> evaluaciones;

    private RegistroEvaluacionesCualitativasAsesorDomain() {}

    public static RegistroEvaluacionesCualitativasAsesorDomain crear(
            UUID evaluacionAsesor, UUID asesor, List<EvaluacionCualitativaAsesorDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCualitativasAsesorDomain();
        var result = new ValidationResult();

        registro.setEvaluacionAsesor(evaluacionAsesor, result);
        registro.setAsesor(asesor, result);
        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
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

    private void setEvaluaciones(List<EvaluacionCualitativaAsesorDomain> evaluaciones, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(evaluaciones,
                EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO, result)) {
            return;
        }

        var items = evaluaciones.stream().map(EvaluacionCualitativaAsesorDomain::getItem).toList();
        if (!ValidatorColeccion.sinDuplicados(items,
                EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.ITEMS_REPETIDOS, result)) {
            return;
        }

        this.evaluaciones = List.copyOf(evaluaciones);
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public List<EvaluacionCualitativaAsesorDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
