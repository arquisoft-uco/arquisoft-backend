package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemCualitativoJuradoCommand(UUID itemCualitativoJurado, String descripcion) {

    public ModificarItemCualitativoJuradoCommand {
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static ModificarItemCualitativoJuradoCommand crear(
            UUID itemCualitativoJurado, String descripcion) {
        var resultado = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcion,
                    EvaluacionesLimits.ItemCualitativoJurado.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                    EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new ModificarItemCualitativoJuradoCommand(itemCualitativoJurado, descripcion);
    }
}
