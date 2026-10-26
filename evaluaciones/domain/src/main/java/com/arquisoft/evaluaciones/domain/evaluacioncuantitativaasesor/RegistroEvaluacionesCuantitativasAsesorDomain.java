package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistroEvaluacionesCuantitativasAsesorDomain {

    private UUID evaluacionAsesor;
    private UUID asesor;
    private List<EvaluacionCuantitativaAsesorDomain> evaluaciones;

    private RegistroEvaluacionesCuantitativasAsesorDomain() {}

    public static RegistroEvaluacionesCuantitativasAsesorDomain crear(
            UUID evaluacionAsesor, UUID asesor, List<EvaluacionCuantitativaAsesorDomain> evaluaciones) {
        var registro = new RegistroEvaluacionesCuantitativasAsesorDomain();
        var result = new ValidationResult();

        registro.setEvaluacionAsesor(evaluacionAsesor, result);
        registro.setAsesor(asesor, result);
        registro.setEvaluaciones(evaluaciones, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setEvaluacionAsesor(UUID evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEvaluaciones(List<EvaluacionCuantitativaAsesorDomain> evaluaciones, ValidationResult result) {
        if (!ValidatorColeccion.noVacia(evaluaciones,
                EvaluacionesFields.RegistroEvaluacionesCuantitativasAsesor.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO, result)) {
            return;
        }

        var items = evaluaciones.stream().map(EvaluacionCuantitativaAsesorDomain::getItem).toList();
        if (!ValidatorColeccion.sinDuplicados(items,
                EvaluacionesFields.RegistroEvaluacionesCuantitativasAsesor.EVALUACIONES,
                EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.ITEMS_REPETIDOS, result)) {
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

    public List<EvaluacionCuantitativaAsesorDomain> getEvaluaciones() {
        return evaluaciones;
    }
}
