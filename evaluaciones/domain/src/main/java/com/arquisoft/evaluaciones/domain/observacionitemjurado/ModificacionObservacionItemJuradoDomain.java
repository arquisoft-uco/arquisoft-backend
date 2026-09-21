package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionObservacionItemJuradoDomain {

    private UUID observacionItemJurado;
    private String descripcion;

    private ModificacionObservacionItemJuradoDomain() {}

    public static ModificacionObservacionItemJuradoDomain crear(UUID observacionItemJurado, String descripcion) {
        var modificacion = new ModificacionObservacionItemJuradoDomain();
        var result = new ValidationResult();

        modificacion.setObservacionItemJurado(observacionItemJurado, result);
        modificacion.setDescripcion(descripcion, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setObservacionItemJurado(UUID observacionItemJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionItemJurado,
                EvaluacionesFields.ObservacionItemJurado.ID,
                EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO, result)) {
            return;
        }
        this.observacionItemJurado = observacionItemJurado;
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

    public UUID getObservacionItemJurado() {
        return observacionItemJurado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
