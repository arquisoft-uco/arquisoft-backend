package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record RegistrarEvaluacionesCualitativasAsesorCommand(
        UUID evaluacionAsesor,
        UUID asesor,
        List<Par<UUID>> evaluaciones) {

    public record Par<T>(T item, T criterio) {}

    public static RegistrarEvaluacionesCualitativasAsesorCommand crear(
            UUID evaluacionAsesor, UUID asesor, List<Par<String>> pares) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result);

        var evaluaciones = validarLote(pares, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionesCualitativasAsesorCommand(evaluacionAsesor, asesor, evaluaciones);
    }

    private static List<Par<UUID>> validarLote(List<Par<String>> pares, ValidationResult result) {
        if (pares.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA,
                            EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES));
            return List.of();
        }

        return IntStream.range(0, pares.size())
                .mapToObj(indice -> validarPar(indice, pares.get(indice), result))
                .toList();
    }

    private static Par<UUID> validarPar(int indice, Par<String> par, ValidationResult result) {
        var campoItem = campo(indice, EvaluacionesFields.EvaluacionCualitativaAsesor.ITEM);
        var campoCriterio = campo(indice, EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO);

        var itemValido = ValidatorTexto.noEnBlanco(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_INVALIDO, result);
        var criterioValido = ValidatorTexto.noEnBlanco(par.criterio(), campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.criterio(), campoCriterio,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_INVALIDO, result);

        var item = itemValido
                ? UtilUUID.generarUUIDDesdeTexto(par.item())
                : UtilUUID.obtenerUUIDPorDefecto();
        var criterio = criterioValido
                ? UtilUUID.generarUUIDDesdeTexto(par.criterio())
                : UtilUUID.obtenerUUIDPorDefecto();

        return new Par<>(item, criterio);
    }

    private static String campo(int indice, String sufijo) {
        return EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES
                + "[" + indice + "]." + sufijo;
    }
}
