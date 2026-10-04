package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record OmitirEvaluacionesCualitativasJuradoCommand(
        UUID evaluacionJurado,
        List<UUID> evaluaciones) {

    public static OmitirEvaluacionesCualitativasJuradoCommand crear(
            String evaluacionJurado, List<String> evaluaciones) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);
        ValidatorUUID.uuidValido(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        var ids = validarLote(UtilColeccion.aplicarPorDefecto(evaluaciones), result);

        result.lanzarSiTieneErroresDeEntrada();

        return new OmitirEvaluacionesCualitativasJuradoCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacionJurado),
                ids);
    }

    private static List<UUID> validarLote(List<String> evaluaciones, ValidationResult result) {
        if (evaluaciones.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA,
                            EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES));
            return List.of();
        }

        return IntStream.range(0, evaluaciones.size())
                .mapToObj(indice -> validarEvaluacion(indice, evaluaciones.get(indice), result))
                .toList();
    }

    private static UUID validarEvaluacion(int indice, String evaluacion, ValidationResult result) {
        var campo = EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES + "[" + indice + "]";

        var valida = ValidatorTexto.noEnBlanco(evaluacion, campo,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.EVALUACION_REQUERIDA, result)
                && ValidatorUUID.uuidValido(evaluacion, campo,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.EVALUACION_INVALIDA, result);

        return valida
                ? UtilUUID.generarUUIDDesdeTexto(evaluacion)
                : UtilUUID.obtenerUUIDPorDefecto();
    }
}
