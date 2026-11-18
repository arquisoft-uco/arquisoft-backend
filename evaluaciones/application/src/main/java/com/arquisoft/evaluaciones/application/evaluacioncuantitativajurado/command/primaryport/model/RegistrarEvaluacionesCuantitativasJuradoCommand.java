package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record RegistrarEvaluacionesCuantitativasJuradoCommand(
        UUID evaluacionJurado,
        UUID jurado,
        List<Par> evaluaciones) {

    public record ParEntrada(String item, Integer puntaje) {}

    public record Par(UUID item, Integer puntaje) {}

    public static RegistrarEvaluacionesCuantitativasJuradoCommand crear(
            UUID evaluacionJurado, UUID jurado, List<ParEntrada> pares) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result);

        var evaluaciones = validarLote(pares, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionesCuantitativasJuradoCommand(evaluacionJurado, jurado, evaluaciones);
    }

    private static List<Par> validarLote(List<ParEntrada> pares, ValidationResult result) {
        if (pares.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCuantitativasJurado.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO,
                    Mensajes.obtener(ValidadorKey.COLECCION_VACIA));
            return List.of();
        }

        return IntStream.range(0, pares.size())
                .mapToObj(indice -> validarPar(indice, pares.get(indice), result))
                .toList();
    }

    private static Par validarPar(int indice, ParEntrada par, ValidationResult result) {
        var campoItem = campo(indice, EvaluacionesFields.EvaluacionCuantitativaJurado.ITEM);
        var campoPuntaje = campo(indice, EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE);

        var itemValido = ValidatorTexto.noEnBlanco(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_INVALIDO, result);
        var puntajeValido = ValidatorObjeto.noNulo(par.puntaje(), campoPuntaje,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO, result)
                && ValidatorNumero.valorEntre(par.puntaje(),
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MIN,
                EvaluacionesLimits.ItemCuantitativoJurado.VALOR_MAX, campoPuntaje,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO, result);

        var item = itemValido
                ? UtilUUID.generarUUIDDesdeTexto(par.item())
                : UtilUUID.obtenerUUIDPorDefecto();
        var puntaje = puntajeValido ? par.puntaje() : UtilNumero.CERO;

        return new Par(item, puntaje);
    }

    private static String campo(int indice, String sufijo) {
        return EvaluacionesFields.RegistroEvaluacionesCuantitativasJurado.EVALUACIONES
                + "[" + indice + "]." + sufijo;
    }
}
