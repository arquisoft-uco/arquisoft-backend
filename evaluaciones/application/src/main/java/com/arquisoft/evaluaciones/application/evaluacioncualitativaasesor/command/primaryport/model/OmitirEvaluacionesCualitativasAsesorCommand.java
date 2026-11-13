package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public record OmitirEvaluacionesCualitativasAsesorCommand(
        UUID evaluacionAsesor,
        UUID asesor,
        List<UUID> items) {

    public static OmitirEvaluacionesCualitativasAsesorCommand crear(
            UUID evaluacionAsesor, UUID asesor, List<String> items) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result);

        var ids = validarLote(UtilColeccion.aplicarPorDefecto(items), result);

        result.lanzarSiTieneErroresDeEntrada();

        return new OmitirEvaluacionesCualitativasAsesorCommand(evaluacionAsesor, asesor, ids);
    }

    private static List<UUID> validarLote(List<String> items, ValidationResult result) {
        if (items.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS,
                    EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.LOTE_VACIO,
                    Mensajes.obtener(ValidadorKey.COLECCION_VACIA));
            return List.of();
        }

        return IntStream.range(0, items.size())
                .mapToObj(indice -> validarItem(indice, items.get(indice), result))
                .toList();
    }

    private static UUID validarItem(int indice, String item, ValidationResult result) {
        var campo = EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS + "[" + indice + "]";

        var valido = ValidatorTexto.noEnBlanco(item, campo,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(item, campo,
                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.ITEM_INVALIDO, result);

        return valido
                ? UtilUUID.generarUUIDDesdeTexto(item)
                : UtilUUID.obtenerUUIDPorDefecto();
    }
}
