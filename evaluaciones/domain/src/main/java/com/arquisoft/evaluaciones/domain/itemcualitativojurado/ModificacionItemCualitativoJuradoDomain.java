package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionItemCualitativoJuradoDomain {

    private UUID itemCualitativoJurado;
    private String descripcion;

    private ModificacionItemCualitativoJuradoDomain() {}

    public static ModificacionItemCualitativoJuradoDomain crear(
            UUID itemCualitativoJurado, String descripcion) {
        var modificacion = new ModificacionItemCualitativoJuradoDomain();
        var resultado = new ValidationResult();

        modificacion.setItemCualitativoJurado(itemCualitativoJurado, resultado);
        modificacion.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setItemCualitativoJurado(UUID itemCualitativoJurado, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCualitativoJurado,
                EvaluacionesFields.ItemCualitativoJurado.ITEM,
                EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCualitativoJurado = itemCualitativoJurado;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        if (!ValidatorTexto.noEnBlanco(
                descripcion,
                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                descripcion,
                EvaluacionesLimits.ItemCualitativoJurado.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = UtilTexto.aplicarTrim(descripcion);
    }

    public UUID getItemCualitativoJurado() {
        return itemCualitativoJurado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
