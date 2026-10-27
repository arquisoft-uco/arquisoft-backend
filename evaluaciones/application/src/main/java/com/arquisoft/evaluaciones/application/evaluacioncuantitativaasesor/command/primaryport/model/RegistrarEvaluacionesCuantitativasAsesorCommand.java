package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model;

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

public record RegistrarEvaluacionesCuantitativasAsesorCommand(
        UUID evaluacionAsesor,
        UUID asesor,
        List<Par> evaluaciones) {

    public record ParEntrada(String item, Integer puntaje) {}

    public record Par(UUID item, Integer puntaje) {}

    public static RegistrarEvaluacionesCuantitativasAsesorCommand crear(
            UUID evaluacionAsesor, UUID asesor, List<ParEntrada> pares) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO, result);

        var evaluaciones = validarLote(pares, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarEvaluacionesCuantitativasAsesorCommand(evaluacionAsesor, asesor, evaluaciones);
    }

    private static List<Par> validarLote(List<ParEntrada> pares, ValidationResult result) {
        if (pares.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.RegistroEvaluacionesCuantitativasAsesor.EVALUACIONES,
                    EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO,
                    Mensajes.obtener(ValidadorKey.COLECCION_VACIA));
            return List.of();
        }

        return IntStream.range(0, pares.size())
                .mapToObj(indice -> validarPar(indice, pares.get(indice), result))
                .toList();
    }

    private static Par validarPar(int indice, ParEntrada par, ValidationResult result) {
        var campoItem = campo(indice, EvaluacionesFields.EvaluacionCuantitativaAsesor.ITEM);
        var campoPuntaje = campo(indice, EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE);

        var itemValido = ValidatorTexto.noEnBlanco(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(par.item(), campoItem,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_INVALIDO, result);
        var puntajeValido = ValidatorObjeto.noNulo(par.puntaje(), campoPuntaje,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO, result)
                && ValidatorNumero.valorEntre(par.puntaje(),
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MIN,
                EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX, campoPuntaje,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO, result);

        var item = itemValido
                ? UtilUUID.generarUUIDDesdeTexto(par.item())
                : UtilUUID.obtenerUUIDPorDefecto();
        var puntaje = puntajeValido ? par.puntaje() : UtilNumero.CERO;

        return new Par(item, puntaje);
    }

    private static String campo(int indice, String sufijo) {
        return EvaluacionesFields.RegistroEvaluacionesCuantitativasAsesor.EVALUACIONES
                + "[" + indice + "]." + sufijo;
    }
}
