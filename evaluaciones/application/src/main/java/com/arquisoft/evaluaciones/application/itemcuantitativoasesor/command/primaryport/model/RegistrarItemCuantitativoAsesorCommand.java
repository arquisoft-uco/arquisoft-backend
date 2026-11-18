package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record RegistrarItemCuantitativoAsesorCommand(
        String nombre, String descripcion, UUID categoria, Integer valor) {

    public RegistrarItemCuantitativoAsesorCommand {
        nombre = UtilTexto.aplicarTrim(nombre);
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static RegistrarItemCuantitativoAsesorCommand crear(
            String nombre, String descripcion, String categoria, Integer valor) {
        var resultado = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombre);
        var descripcionRecortada = UtilTexto.aplicarTrim(descripcion);
        if (ValidatorTexto.noEnBlanco(
                nombreRecortado,
                EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_REQUERIDO,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    nombreRecortado,
                    EvaluacionesLimits.ItemCuantitativoAsesor.NOMBRE_MAX,
                    EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                    EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                    resultado);
        }

        if (ValidatorTexto.noEnBlanco(
                descripcionRecortada,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    descripcionRecortada,
                    EvaluacionesLimits.ItemCuantitativoAsesor.DESCRIPCION_MAX,
                    EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                    EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                    resultado);
        }

        if (ValidatorTexto.noEnBlanco(
                categoria,
                EvaluacionesFields.ItemCuantitativoAsesor.CATEGORIA,
                EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA,
                resultado)) {
            ValidatorUUID.uuidValido(
                    categoria,
                    EvaluacionesFields.ItemCuantitativoAsesor.CATEGORIA,
                    EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA,
                    resultado);
        }

        if (ValidatorObjeto.noNulo(
                valor,
                EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_REQUERIDO,
                resultado)) {
            ValidatorNumero.valorEntre(
                    valor,
                    EvaluacionesLimits.ItemCuantitativoAsesor.VALOR_MIN,
                    EvaluacionesLimits.ItemCuantitativoAsesor.VALOR_MAX,
                    EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                    EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_FUERA_DE_RANGO,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RegistrarItemCuantitativoAsesorCommand(
                nombreRecortado, descripcionRecortada, UtilUUID.generarUUIDDesdeTexto(categoria), valor);
    }
}
