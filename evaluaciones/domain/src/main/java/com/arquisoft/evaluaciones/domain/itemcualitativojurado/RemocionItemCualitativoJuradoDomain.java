package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class RemocionItemCualitativoJuradoDomain {

    private UUID itemCualitativoJurado;
    private Instant eliminadoEn;

    private RemocionItemCualitativoJuradoDomain() {}

    public static RemocionItemCualitativoJuradoDomain crear(UUID itemCualitativoJurado) {
        var remocion = new RemocionItemCualitativoJuradoDomain();
        var resultado = new ValidationResult();

        remocion.setItemCualitativoJurado(itemCualitativoJurado, resultado);
        remocion.setEliminadoEn();

        resultado.lanzarSiTieneErrores();
        return remocion;
    }

    private void setItemCualitativoJurado(UUID itemCualitativoJurado, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCualitativoJurado,
                EvaluacionesFields.ItemCualitativoJurado.ITEM,
                EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCualitativoJurado = itemCualitativoJurado;
    }

    private void setEliminadoEn() {
        this.eliminadoEn = UtilFecha.generarInstanteActual();
    }

    public UUID getItemCualitativoJurado() {
        return itemCualitativoJurado;
    }

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }
}
