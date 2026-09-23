package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionItemCuantitativoJuradoDomain {

    private UUID itemCuantitativoJurado;

    private RemocionItemCuantitativoJuradoDomain() {}

    public static RemocionItemCuantitativoJuradoDomain crear(UUID itemCuantitativoJurado) {
        var remocion = new RemocionItemCuantitativoJuradoDomain();
        var resultado = new ValidationResult();

        remocion.setItemCuantitativoJurado(itemCuantitativoJurado, resultado);

        resultado.lanzarSiTieneErrores();
        return remocion;
    }

    private void setItemCuantitativoJurado(UUID itemCuantitativoJurado, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCuantitativoJurado,
                EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                EvaluacionesCodes.ItemCuantitativoJurado.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCuantitativoJurado = itemCuantitativoJurado;
    }

    public UUID getItemCuantitativoJurado() {
        return itemCuantitativoJurado;
    }
}
