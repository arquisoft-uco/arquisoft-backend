package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ParEvaluacionCualitativaJuradoCommand(UUID item, UUID criterio) {

    public static ParEvaluacionCualitativaJuradoCommand crear(
            int indice, String item, String criterio, ValidationResult result) {
        var campoItem = campo(indice, EvaluacionesFields.EvaluacionCualitativaJurado.ITEM);
        var campoCriterio = campo(indice, EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO);

        UUID itemId = null;
        if (ValidatorTexto.noEnBlanco(item, campoItem,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(item, campoItem,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_INVALIDO, result)) {
            itemId = UtilUUID.generarUUIDDesdeTexto(item);
        }

        UUID criterioId = null;
        if (ValidatorTexto.noEnBlanco(criterio, campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(criterio, campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_INVALIDO, result)) {
            criterioId = UtilUUID.generarUUIDDesdeTexto(criterio);
        }

        return new ParEvaluacionCualitativaJuradoCommand(itemId, criterioId);
    }

    private static String campo(int indice, String sufijo) {
        return EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES
                + "[" + indice + "]." + sufijo;
    }
}
