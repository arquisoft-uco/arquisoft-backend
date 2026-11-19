package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class RemocionItemCuantitativoAsesorDomain {

    private UUID itemCuantitativoAsesor;
    private Instant eliminadoEn;

    private RemocionItemCuantitativoAsesorDomain() {}

    public static RemocionItemCuantitativoAsesorDomain crear(UUID itemCuantitativoAsesor) {
        var remocion = new RemocionItemCuantitativoAsesorDomain();
        var resultado = new ValidationResult();

        remocion.setItemCuantitativoAsesor(itemCuantitativoAsesor, resultado);
        remocion.setEliminadoEn();

        resultado.lanzarSiTieneErrores();
        return remocion;
    }

    private void setItemCuantitativoAsesor(UUID itemCuantitativoAsesor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCuantitativoAsesor,
                EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCuantitativoAsesor = itemCuantitativoAsesor;
    }

    private void setEliminadoEn() {
        this.eliminadoEn = UtilFecha.generarInstanteActual();
    }

    public UUID getItemCuantitativoAsesor() {
        return itemCuantitativoAsesor;
    }

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }
}
