package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record RegistrarItemCualitativoJuradoCommand(String nombre, String descripcion) {

    public RegistrarItemCualitativoJuradoCommand {
        nombre = UtilTexto.aplicarTrim(nombre);
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static RegistrarItemCualitativoJuradoCommand crear(
            String nombre, String descripcion) {
        var resultado = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    nombre,
                    EvaluacionesLimits.ItemCualitativoJurado.NOMBRE_MAX,
                    EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                    EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DEMASIADO_LARGO,
                    resultado);
        }

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
        return new RegistrarItemCualitativoJuradoCommand(nombre, descripcion);
    }
}
