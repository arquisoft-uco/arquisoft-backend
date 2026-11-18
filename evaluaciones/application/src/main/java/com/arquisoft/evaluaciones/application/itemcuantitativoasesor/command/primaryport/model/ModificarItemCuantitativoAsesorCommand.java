package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemCuantitativoAsesorCommand(UUID itemCuantitativoAsesor, String descripcion) {

    public ModificarItemCuantitativoAsesorCommand {
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static ModificarItemCuantitativoAsesorCommand crear(
            UUID itemCuantitativoAsesor, String descripcion) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCuantitativoAsesor,
                EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO,
                resultado);

        if (ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcion,
                    EvaluacionesLimits.ItemCuantitativoAsesor.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                    EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new ModificarItemCuantitativoAsesorCommand(itemCuantitativoAsesor, descripcion);
    }
}
