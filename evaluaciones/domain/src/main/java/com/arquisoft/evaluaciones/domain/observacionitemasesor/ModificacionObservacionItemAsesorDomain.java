package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionObservacionItemAsesorDomain {

    private UUID observacion;
    private UUID asesor;
    private String descripcion;

    private ModificacionObservacionItemAsesorDomain() {}

    public static ModificacionObservacionItemAsesorDomain crear(UUID observacion, UUID asesor, String descripcion) {
        var modificacion = new ModificacionObservacionItemAsesorDomain();
        var result = new ValidationResult();

        modificacion.setObservacion(observacion, result);
        modificacion.setAsesor(asesor, result);
        modificacion.setDescripcion(descripcion, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setObservacion(UUID observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                EvaluacionesFields.ObservacionItemAsesor.ID,
                EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
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

    public UUID getObservacion() {
        return observacion;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
