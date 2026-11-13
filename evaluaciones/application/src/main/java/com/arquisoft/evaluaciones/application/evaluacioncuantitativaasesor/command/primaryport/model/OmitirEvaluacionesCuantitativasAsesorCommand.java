package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model;

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

public record OmitirEvaluacionesCuantitativasAsesorCommand(
        UUID evaluacionAsesor,
        UUID asesor,
        List<UUID> items) {

    public static OmitirEvaluacionesCuantitativasAsesorCommand crear(
            UUID evaluacionAsesor, UUID asesor, List<String> items) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO, result);

        var ids = validarLote(UtilColeccion.aplicarPorDefecto(items), result);

        result.lanzarSiTieneErroresDeEntrada();

        return new OmitirEvaluacionesCuantitativasAsesorCommand(evaluacionAsesor, asesor, ids);
    }

    private static List<UUID> validarLote(List<String> items, ValidationResult result) {
        if (items.isEmpty()) {
            result.agregarError(
                    EvaluacionesFields.OmisionEvaluacionesCuantitativasAsesor.ITEMS,
                    EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.LOTE_VACIO,
                    Mensajes.obtener(ValidadorKey.COLECCION_VACIA));
            return List.of();
        }

        return IntStream.range(0, items.size())
                .mapToObj(indice -> validarItem(indice, items.get(indice), result))
                .toList();
    }

    private static UUID validarItem(int indice, String item, ValidationResult result) {
        var campo = EvaluacionesFields.OmisionEvaluacionesCuantitativasAsesor.ITEMS + "[" + indice + "]";

        var valido = ValidatorTexto.noEnBlanco(item, campo,
                EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.ITEM_REQUERIDO, result)
                && ValidatorUUID.uuidValido(item, campo,
                EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.ITEM_INVALIDO, result);

        return valido
                ? UtilUUID.generarUUIDDesdeTexto(item)
                : UtilUUID.obtenerUUIDPorDefecto();
    }
}
