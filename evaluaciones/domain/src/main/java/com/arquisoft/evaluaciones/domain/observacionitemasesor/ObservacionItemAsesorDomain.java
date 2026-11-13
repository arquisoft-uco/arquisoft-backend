package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ObservacionItemAsesorDomain {

    public static final ObservacionItemAsesorDomain VACIO = new ObservacionItemAsesorDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO);

    private UUID id;
    private UUID evaluacionCuantitativaAsesor;
    private String descripcion;

    private ObservacionItemAsesorDomain() {}

    private ObservacionItemAsesorDomain(UUID id, UUID evaluacionCuantitativaAsesor, String descripcion) {
        this.id = id;
        this.evaluacionCuantitativaAsesor = evaluacionCuantitativaAsesor;
        this.descripcion = descripcion;
    }

    public static ObservacionItemAsesorDomain crear(UUID evaluacionCuantitativaAsesor, String descripcion) {
        var observacion = new ObservacionItemAsesorDomain();
        var result = new ValidationResult();

        observacion.setId();
        observacion.setEvaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor, result);
        observacion.setDescripcion(descripcion, result);

        result.lanzarSiTieneErrores();
        return observacion;
    }

    public static ObservacionItemAsesorDomain reconstruir(
            UUID id, UUID evaluacionCuantitativaAsesor, String descripcion) {
        return new ObservacionItemAsesorDomain(id, evaluacionCuantitativaAsesor, descripcion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionCuantitativaAsesor(UUID evaluacionCuantitativaAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA, result)) {
            return;
        }
        this.evaluacionCuantitativaAsesor = evaluacionCuantitativaAsesor;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                EvaluacionesFields.ObservacionItemAsesor.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada,
                EvaluacionesLimits.ObservacionItemAsesor.DESCRIPCION_MAX,
                EvaluacionesFields.ObservacionItemAsesor.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionCuantitativaAsesor() {
        return evaluacionCuantitativaAsesor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
