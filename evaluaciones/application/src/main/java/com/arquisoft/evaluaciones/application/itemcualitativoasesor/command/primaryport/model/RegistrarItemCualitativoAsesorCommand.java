package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record RegistrarItemCualitativoAsesorCommand(String nombre, String descripcion) {

    public static RegistrarItemCualitativoAsesorCommand crear(String nombre, String descripcion) {
        var resultado = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombre);
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);

        if (ValidatorTexto.noEnBlanco(
                nombreRecortado,
                EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_REQUERIDO,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    nombreRecortado,
                    EvaluacionesLimits.ItemCualitativoAsesor.NOMBRE_MAX,
                    EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                    EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                    resultado);
        }

        if (ValidatorTexto.noEnBlanco(
                descripcionRecortada,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcionRecortada,
                    EvaluacionesLimits.ItemCualitativoAsesor.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                    EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RegistrarItemCualitativoAsesorCommand(nombreRecortado, descripcionRecortada);
    }
}
