package com.arquisoft.evaluaciones.domain.observacionitemjurado;

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

public final class ObservacionItemJuradoDomain {

    private UUID id;
    private UUID evaluacionCuantitativaJurado;
    private String descripcion;

    private ObservacionItemJuradoDomain() {}

    private ObservacionItemJuradoDomain(UUID id, UUID evaluacionCuantitativaJurado, String descripcion) {
        this.id = id;
        this.evaluacionCuantitativaJurado = evaluacionCuantitativaJurado;
        this.descripcion = descripcion;
    }

    public static ObservacionItemJuradoDomain crear(UUID evaluacionCuantitativaJurado, String descripcion) {
        var observacion = new ObservacionItemJuradoDomain();
        var result = new ValidationResult();

        observacion.setId();
        observacion.setEvaluacionCuantitativaJurado(evaluacionCuantitativaJurado, result);
        observacion.setDescripcion(descripcion, result);

        result.lanzarSiTieneErrores();
        return observacion;
    }

    public static ObservacionItemJuradoDomain reconstruir(
            UUID id, UUID evaluacionCuantitativaJurado, String descripcion) {
        return new ObservacionItemJuradoDomain(id, evaluacionCuantitativaJurado, descripcion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionCuantitativaJurado(UUID evaluacionCuantitativaJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO_REQUERIDA, result)) {
            return;
        }
        this.evaluacionCuantitativaJurado = evaluacionCuantitativaJurado;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada,
                EvaluacionesLimits.ObservacionItemJurado.DESCRIPCION_MAX,
                EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionCuantitativaJurado() {
        return evaluacionCuantitativaJurado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
