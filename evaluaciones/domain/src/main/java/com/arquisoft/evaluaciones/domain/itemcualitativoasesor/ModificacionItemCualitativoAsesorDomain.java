package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionItemCualitativoAsesorDomain {

    private UUID itemCualitativoAsesor;
    private String descripcion;

    private ModificacionItemCualitativoAsesorDomain() {}

    public static ModificacionItemCualitativoAsesorDomain crear(
            UUID itemCualitativoAsesor, String descripcion) {
        var modificacion = new ModificacionItemCualitativoAsesorDomain();
        var resultado = new ValidationResult();

        modificacion.setItemCualitativoAsesor(itemCualitativoAsesor, resultado);
        modificacion.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setItemCualitativoAsesor(UUID itemCualitativoAsesor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCualitativoAsesor,
                EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCualitativoAsesor = itemCualitativoAsesor;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        var recortado = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(
                recortado,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortado,
                EvaluacionesLimits.ItemCualitativoAsesor.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = recortado;
    }

    public UUID getItemCualitativoAsesor() {
        return itemCualitativoAsesor;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
