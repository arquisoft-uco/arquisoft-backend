package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EvaluacionAsesorDomain {

    public static final EvaluacionAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private UUID evaluacion;
    private UUID asesor;

    private EvaluacionAsesorDomain() {}

    public static EvaluacionAsesorDomain crear(UUID evaluacion, UUID asesor) {
        var evaluacionAsesor = new EvaluacionAsesorDomain();
        var result = new ValidationResult();

        evaluacionAsesor.setId();
        evaluacionAsesor.setEvaluacion(evaluacion, result);
        evaluacionAsesor.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return evaluacionAsesor;
    }

    public static EvaluacionAsesorDomain reconstruir(UUID id, UUID evaluacion, UUID asesor) {
        var evaluacionAsesor = new EvaluacionAsesorDomain();
        evaluacionAsesor.id = id;
        evaluacionAsesor.evaluacion = evaluacion;
        evaluacionAsesor.asesor = asesor;
        return evaluacionAsesor;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionAsesor.EVALUACION,
                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public boolean tieneAsesor(UUID asesor) {
        return this.asesor.equals(asesor);
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
