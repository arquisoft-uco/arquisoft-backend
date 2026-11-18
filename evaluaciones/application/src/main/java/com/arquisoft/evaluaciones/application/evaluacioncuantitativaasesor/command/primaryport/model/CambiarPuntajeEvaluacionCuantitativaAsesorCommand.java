package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record CambiarPuntajeEvaluacionCuantitativaAsesorCommand(
        UUID evaluacionCuantitativaAsesor, Integer nuevoPuntaje, UUID asesor) {

    public static CambiarPuntajeEvaluacionCuantitativaAsesorCommand crear(
            UUID evaluacionCuantitativaAsesor, Number nuevoPuntaje, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ID,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ID_REQUERIDO, result);

        if (ValidatorObjeto.noNulo(nuevoPuntaje,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO, result)) {
            validarPuntajeEntero(nuevoPuntaje, result);
        }

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarPuntajeEvaluacionCuantitativaAsesorCommand(
                evaluacionCuantitativaAsesor, nuevoPuntaje.intValue(), asesor);
    }

    private static void validarPuntajeEntero(Number nuevoPuntaje, ValidationResult result) {
        if (UtilNumero.tieneParteDecimal(nuevoPuntaje)) {
            result.agregarError(
                    EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                    EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_NO_ENTERO,
                    Mensajes.formatear(EvaluacionCuantitativaAsesorKey.ERROR_PUNTAJE_NO_ENTERO));
            return;
        }
        ValidatorNumero.valorEntre(nuevoPuntaje,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MIN,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO, result);
    }
}
