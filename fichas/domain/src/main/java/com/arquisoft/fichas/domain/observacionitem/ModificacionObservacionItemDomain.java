package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionObservacionItemDomain {

    private UUID observacionItem;
    private String observacion;
    private UUID asesorFicha;

    private ModificacionObservacionItemDomain() {}

    public static ModificacionObservacionItemDomain crear(UUID observacionItem, String observacion, UUID asesorFicha) {
        var modificacion = new ModificacionObservacionItemDomain();
        var result = new ValidationResult();

        modificacion.setObservacionItem(observacionItem, result);
        modificacion.setObservacion(observacion, result);
        modificacion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setObservacionItem(UUID observacionItem, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionItem,
                FichasFields.ObservacionItem.OBSERVACION_ITEM,
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO, result)) {
            return;
        }
        this.observacionItem = observacionItem;
    }

    private void setObservacion(String observacion, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(observacion);
        if (!ValidatorTexto.noEnBlanco(recortado,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, FichasLimits.ObservacionItem.OBSERVACION_MAX,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.observacion = recortado;
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionItem.ASESOR_FICHA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public UUID getObservacionItem() {
        return observacionItem;
    }

    public String getObservacion() {
        return observacion;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
