package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record CambiarPuntajeEvaluacionCuantitativaJuradoCommand(
        UUID evaluacionCuantitativaJurado, Integer nuevoPuntaje, UUID jurado) {

    public static CambiarPuntajeEvaluacionCuantitativaJuradoCommand crear(
            UUID evaluacionCuantitativaJurado, Number nuevoPuntaje, UUID jurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.ID,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ID_REQUERIDO, result);

        if (ValidatorObjeto.noNulo(nuevoPuntaje,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO, result)) {
            validarPuntajeEntero(nuevoPuntaje, result);
        }

        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarPuntajeEvaluacionCuantitativaJuradoCommand(
                evaluacionCuantitativaJurado, nuevoPuntaje.intValue(), jurado);
    }

    private static void validarPuntajeEntero(Number nuevoPuntaje, ValidationResult result) {
        if (UtilNumero.tieneParteDecimal(nuevoPuntaje)) {
            result.agregarError(
                    EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                    EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_NO_ENTERO,
                    Mensajes.formatear(EvaluacionCuantitativaJuradoKey.ERROR_PUNTAJE_NO_ENTERO));
            return;
        }
        ValidatorNumero.valorEntre(nuevoPuntaje,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX,
                EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO, result);
    }
}
