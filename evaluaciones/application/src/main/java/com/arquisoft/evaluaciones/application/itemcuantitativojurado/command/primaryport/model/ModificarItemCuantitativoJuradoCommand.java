package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemCuantitativoJuradoCommand(UUID itemCuantitativoJurado, String descripcion) {

    public ModificarItemCuantitativoJuradoCommand {
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static ModificarItemCuantitativoJuradoCommand crear(
            UUID itemCuantitativoJurado, String descripcion) {
        var resultado = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcion,
                    EvaluacionesLimits.ItemCuantitativoJurado.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                    EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new ModificarItemCuantitativoJuradoCommand(itemCuantitativoJurado, descripcion);
    }
}
