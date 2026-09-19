package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record RegistrarEvaluacionesCualitativasJuradoCommand(
        UUID evaluacionJurado,
        List<ParEvaluacionCualitativaJuradoCommand> evaluaciones) {

    public record ParEntrada(String item, String criterio) {}

    public static RegistrarEvaluacionesCualitativasJuradoCommand crear(
            String evaluacionJurado, List<ParEntrada> pares) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);
        ValidatorUUID.uuidValido(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        List<ParEvaluacionCualitativaJuradoCommand> evaluaciones = validarLote(pares, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionesCualitativasJuradoCommand(
                UtilUUID.generarUUIDDesdeTexto(evaluacionJurado),
                evaluaciones);
    }

    private static List<ParEvaluacionCualitativaJuradoCommand> validarLote(
            List<ParEntrada> pares, ValidationResult result) {
        if (UtilObjeto.esNulo(pares)) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_REQUERIDO,
                    Mensajes.formatear(ValidadorKey.NO_NULO,
                            EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES));
            return List.of();
        }

        if (pares.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA,
                            EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES));
            return List.of();
        }

        List<ParEvaluacionCualitativaJuradoCommand> evaluaciones = new ArrayList<>();
        for (int indice = 0; indice < pares.size(); indice++) {
            ParEntrada par = pares.get(indice);
            if (UtilObjeto.esNulo(par)) {
                result.agregarError(
                        EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES
                                + "[" + indice + "]",
                        EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.PAR_REQUERIDO,
                        Mensajes.formatear(ValidadorKey.NO_NULO,
                                EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES
                                        + "[" + indice + "]"));
                continue;
            }
            evaluaciones.add(ParEvaluacionCualitativaJuradoCommand.crear(
                    indice, par.item(), par.criterio(), result));
        }
        return evaluaciones;
    }
}
