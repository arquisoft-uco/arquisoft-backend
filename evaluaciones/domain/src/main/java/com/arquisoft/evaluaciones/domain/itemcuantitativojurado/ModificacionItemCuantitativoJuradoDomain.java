package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionItemCuantitativoJuradoDomain {

    private UUID itemCuantitativoJurado;
    private String descripcion;

    private ModificacionItemCuantitativoJuradoDomain() {}

    public static ModificacionItemCuantitativoJuradoDomain crear(
            UUID itemCuantitativoJurado, String descripcion) {
        var modificacion = new ModificacionItemCuantitativoJuradoDomain();
        var resultado = new ValidationResult();

        modificacion.setItemCuantitativoJurado(itemCuantitativoJurado, resultado);
        modificacion.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setItemCuantitativoJurado(UUID itemCuantitativoJurado, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCuantitativoJurado,
                EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                EvaluacionesCodes.ItemCuantitativoJurado.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCuantitativoJurado = itemCuantitativoJurado;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.ItemCuantitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public UUID getItemCuantitativoJurado() {
        return itemCuantitativoJurado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
