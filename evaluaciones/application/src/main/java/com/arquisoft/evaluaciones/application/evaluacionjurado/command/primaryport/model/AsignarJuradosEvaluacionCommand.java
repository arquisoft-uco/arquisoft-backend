package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record AsignarJuradosEvaluacionCommand(UUID evaluacion, List<UUID> jurados) {

    public static AsignarJuradosEvaluacionCommand crear(String evaluacion, List<String> jurados) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result);
        ValidatorUUID.uuidValido(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result);

        var juradosValidados = validarLote(jurados, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarJuradosEvaluacionCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacion),
                juradosValidados);
    }

    private static List<UUID> validarLote(List<String> jurados, ValidationResult result) {
        if (jurados.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                    EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA,
                            EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS));
            return List.of();
        }
        return IntStream.range(0, jurados.size())
                .mapToObj(indice -> validarJurado(indice, jurados.get(indice), result))
                .toList();
    }

    private static UUID validarJurado(int indice, String jurado, ValidationResult result) {
        var campo = EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS + "[" + indice + "]";
        var valido = ValidatorTexto.noEnBlanco(jurado, campo,
                EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(jurado, campo,
                EvaluacionesCodes.EvaluacionJurado.JURADO_INVALIDO, result);
        return valido
                ? UtilUUID.generarUUIDDesdeTexto(jurado)
                : UtilUUID.obtenerUUIDPorDefecto();
    }
}
