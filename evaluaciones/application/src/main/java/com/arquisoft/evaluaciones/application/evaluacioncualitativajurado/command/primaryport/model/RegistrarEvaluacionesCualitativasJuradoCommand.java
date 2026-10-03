package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

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

public record RegistrarEvaluacionesCualitativasJuradoCommand(
        UUID evaluacionJurado,
        List<Par<UUID>> evaluaciones) {

    public record Par<T>(T item, T criterio) {}

    public static RegistrarEvaluacionesCualitativasJuradoCommand crear(
            String evaluacionJurado, List<Par<String>> pares) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);
        ValidatorUUID.uuidValido(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        var evaluaciones = validarLote(pares, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionesCualitativasJuradoCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacionJurado),
                evaluaciones);
    }

    private static List<Par<UUID>> validarLote(List<Par<String>> pares, ValidationResult result) {
        if (pares.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA,
                            EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES));
            return List.of();
        }

        return IntStream.range(0, pares.size())
                .mapToObj(indice -> validarPar(indice, pares.get(indice), result))
                .toList();
    }

    private static Par<UUID> validarPar(int indice, Par<String> par, ValidationResult result) {
        var campoItem = campo(indice, EvaluacionesFields.EvaluacionCualitativaJurado.ITEM);
        var campoCriterio = campo(indice, EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO);

        var itemValido = ValidatorTexto.noEnBlanco(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_INVALIDO, result);
        var criterioValido = ValidatorTexto.noEnBlanco(par.criterio(), campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.criterio(), campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_INVALIDO, result);

        var item = itemValido
                ? UtilUUID.generarUUIDDesdeTexto(par.item())
                : UtilUUID.obtenerUUIDPorDefecto();
        var criterio = criterioValido
                ? UtilUUID.generarUUIDDesdeTexto(par.criterio())
                : UtilUUID.obtenerUUIDPorDefecto();

        return new Par<>(item, criterio);
    }

    private static String campo(int indice, String sufijo) {
        return EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES
                + "[" + indice + "]." + sufijo;
    }
}
