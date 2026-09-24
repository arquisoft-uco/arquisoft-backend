package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

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

public record RegistrarItemCuantitativoJuradoCommand(
        String nombre, String descripcion, UUID categoria, Integer valor) {

    public RegistrarItemCuantitativoJuradoCommand {
        nombre = UtilTexto.aplicarTrim(nombre);
        descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public static RegistrarItemCuantitativoJuradoCommand crear(
            String nombre, String descripcion, String categoria, Integer valor) {
        var resultado = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(
                nombre,
                EvaluacionesFields.ItemCuantitativoJurado.NOMBRE,
                EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_REQUERIDO,
                resultado)) {
            ValidatorLongitud.longitudMaxima(
                    nombre,
                    EvaluacionesLimits.ItemCuantitativoJurado.NOMBRE_MAX,
                    EvaluacionesFields.ItemCuantitativoJurado.NOMBRE,
                    EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_DEMASIADO_LARGO,
                    resultado);
        }

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

        if (ValidatorTexto.noEnBlanco(
                categoria,
                EvaluacionesFields.ItemCuantitativoJurado.CATEGORIA,
                EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA,
                resultado)) {
            ValidatorUUID.uuidValido(
                    categoria,
                    EvaluacionesFields.ItemCuantitativoJurado.CATEGORIA,
                    EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA,
                    resultado);
        }

        if (ValidatorObjeto.noNulo(
                valor,
                EvaluacionesFields.ItemCuantitativoJurado.VALOR,
                EvaluacionesCodes.ItemCuantitativoJurado.VALOR_REQUERIDO,
                resultado)) {
            ValidatorNumero.valorEntre(
                    valor,
                    EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                    EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX,
                    EvaluacionesFields.ItemCuantitativoJurado.VALOR,
                    EvaluacionesCodes.ItemCuantitativoJurado.VALOR_FUERA_DE_RANGO,
                    resultado);
        }

        resultado.lanzarSiTieneErroresDeEntrada();
        return new RegistrarItemCuantitativoJuradoCommand(
                nombre, descripcion, UtilUUID.generarUUIDDesdeTexto(categoria), valor);
    }
}
