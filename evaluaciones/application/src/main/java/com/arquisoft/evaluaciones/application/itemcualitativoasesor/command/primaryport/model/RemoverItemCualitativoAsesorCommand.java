package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverItemCualitativoAsesorCommand(UUID itemCualitativoAsesor) {

    public static RemoverItemCualitativoAsesorCommand crear(UUID itemCualitativoAsesor) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCualitativoAsesor,
                EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RemoverItemCualitativoAsesorCommand(itemCualitativoAsesor);
    }
}
