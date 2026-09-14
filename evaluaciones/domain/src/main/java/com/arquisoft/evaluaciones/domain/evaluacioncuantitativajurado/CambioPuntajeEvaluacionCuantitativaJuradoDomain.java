package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioPuntajeEvaluacionCuantitativaJuradoDomain {

    private UUID evaluacionCuantitativaJurado;
    private UUID jurado;
    private Integer nuevoPuntaje;

    private CambioPuntajeEvaluacionCuantitativaJuradoDomain() {}

    public static CambioPuntajeEvaluacionCuantitativaJuradoDomain crear(
            UUID evaluacionCuantitativaJurado, UUID jurado, Integer nuevoPuntaje) {
        var cambio = new CambioPuntajeEvaluacionCuantitativaJuradoDomain();
        var result = new ValidationResult();

        cambio.setEvaluacionCuantitativaJurado(evaluacionCuantitativaJurado, result);
        cambio.setJurado(jurado, result);
        cambio.setNuevoPuntaje(nuevoPuntaje, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setEvaluacionCuantitativaJurado(UUID evaluacionCuantitativaJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.ID,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ID_REQUERIDO, result)) {
            return;
        }
        this.evaluacionCuantitativaJurado = evaluacionCuantitativaJurado;
    }

    private void setJurado(UUID jurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result)) {
            return;
        }
        this.jurado = jurado;
    }

    private void setNuevoPuntaje(Integer nuevoPuntaje, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(nuevoPuntaje,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorEntre(nuevoPuntaje,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO, result)) {
            return;
        }
        this.nuevoPuntaje = nuevoPuntaje;
    }

    public UUID getEvaluacionCuantitativaJurado() {
        return evaluacionCuantitativaJurado;
    }

    public UUID getJurado() {
        return jurado;
    }

    public Integer getNuevoPuntaje() {
        return nuevoPuntaje;
    }
}
