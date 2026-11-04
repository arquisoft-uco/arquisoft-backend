package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverItemCualitativoJuradoCommand(UUID itemCualitativoJurado) {

    public static RemoverItemCualitativoJuradoCommand crear(UUID itemCualitativoJurado) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCualitativoJurado,
                EvaluacionesFields.ItemCualitativoJurado.ITEM,
                EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RemoverItemCualitativoJuradoCommand(itemCualitativoJurado);
    }
}
