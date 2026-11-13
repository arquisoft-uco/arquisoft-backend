package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionItemCualitativoAsesorDomain {

    private UUID itemCualitativoAsesor;

    private RemocionItemCualitativoAsesorDomain() {}

    public static RemocionItemCualitativoAsesorDomain crear(UUID itemCualitativoAsesor) {
        var remocion = new RemocionItemCualitativoAsesorDomain();
        var resultado = new ValidationResult();

        remocion.setItemCualitativoAsesor(itemCualitativoAsesor, resultado);

        resultado.lanzarSiTieneErrores();
        return remocion;
    }

    private void setItemCualitativoAsesor(UUID itemCualitativoAsesor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCualitativoAsesor,
                EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCualitativoAsesor = itemCualitativoAsesor;
    }

    public UUID getItemCualitativoAsesor() {
        return itemCualitativoAsesor;
    }
}
