package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverItemCuantitativoAsesorCommand(UUID itemCuantitativoAsesor) {

    public static RemoverItemCuantitativoAsesorCommand crear(UUID itemCuantitativoAsesor) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCuantitativoAsesor,
                EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RemoverItemCuantitativoAsesorCommand(itemCuantitativoAsesor);
    }
}
