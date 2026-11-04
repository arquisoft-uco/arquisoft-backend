package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverItemCuantitativoJuradoCommand(UUID itemCuantitativoJurado) {

    public static RemoverItemCuantitativoJuradoCommand crear(UUID itemCuantitativoJurado) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCuantitativoJurado,
                EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                EvaluacionesCodes.ItemCuantitativoJurado.ITEM_ID_REQUERIDO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RemoverItemCuantitativoJuradoCommand(itemCuantitativoJurado);
    }
}
