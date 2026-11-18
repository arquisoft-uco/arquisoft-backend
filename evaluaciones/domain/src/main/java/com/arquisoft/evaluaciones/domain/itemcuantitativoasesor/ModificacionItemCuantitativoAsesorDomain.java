package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionItemCuantitativoAsesorDomain {

    private UUID itemCuantitativoAsesor;
    private String descripcion;

    private ModificacionItemCuantitativoAsesorDomain() {}

    public static ModificacionItemCuantitativoAsesorDomain crear(
            UUID itemCuantitativoAsesor, String descripcion) {
        var modificacion = new ModificacionItemCuantitativoAsesorDomain();
        var resultado = new ValidationResult();

        modificacion.setItemCuantitativoAsesor(itemCuantitativoAsesor, resultado);
        modificacion.setDescripcion(descripcion, resultado);

        resultado.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setItemCuantitativoAsesor(UUID itemCuantitativoAsesor, ValidationResult resultado) {
        if (!ValidatorObjeto.noNulo(
                itemCuantitativoAsesor,
                EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO,
                resultado)) {
            return;
        }
        this.itemCuantitativoAsesor = itemCuantitativoAsesor;
    }

    private void setDescripcion(String descripcion, ValidationResult resultado) {
        var recortado = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(
                recortado,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA,
                resultado)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(
                recortado,
                EvaluacionesLimits.ItemCuantitativoAsesor.DESCRIPCION_MAX,
                EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA,
                resultado)) {
            return;
        }
        this.descripcion = recortado;
    }

    public UUID getItemCuantitativoAsesor() {
        return itemCuantitativoAsesor;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
