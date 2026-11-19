package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemCualitativoJuradoCommand(UUID itemCualitativoJurado, String descripcion, Integer peso) {

    public ModificarItemCualitativoJuradoCommand {
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static ModificarItemCualitativoJuradoCommand crear(
            UUID itemCualitativoJurado, String descripcion, Integer peso) {
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

        if (ValidatorObjeto.noNulo(
                peso,
                EvaluacionesFields.ItemCualitativoJurado.PESO,
                EvaluacionesCodes.ItemCualitativoJurado.PESO_REQUERIDO,
                resultado)) {
            ValidatorNumero.valorEntre(
                    peso,
                    EvaluacionesLimits.ItemCualitativoJurado.PESO_MIN,
                    EvaluacionesLimits.ItemCualitativoJurado.PESO_MAX,
                    EvaluacionesFields.ItemCualitativoJurado.PESO,
                    EvaluacionesCodes.ItemCualitativoJurado.PESO_FUERA_DE_RANGO,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new ModificarItemCualitativoJuradoCommand(itemCualitativoJurado, descripcion, peso);
    }
}
