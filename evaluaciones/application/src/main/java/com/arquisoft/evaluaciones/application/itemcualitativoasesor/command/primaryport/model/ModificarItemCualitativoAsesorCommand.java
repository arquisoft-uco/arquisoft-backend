package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemCualitativoAsesorCommand(UUID itemCualitativoAsesor, String descripcion) {

    public ModificarItemCualitativoAsesorCommand {
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static ModificarItemCualitativoAsesorCommand crear(
            UUID itemCualitativoAsesor, String descripcion) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                itemCualitativoAsesor,
                EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO,
                resultado);

        if (ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcion,
                    EvaluacionesLimits.ItemCualitativoAsesor.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                    EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new ModificarItemCualitativoAsesorCommand(itemCualitativoAsesor, descripcion);
    }
}
