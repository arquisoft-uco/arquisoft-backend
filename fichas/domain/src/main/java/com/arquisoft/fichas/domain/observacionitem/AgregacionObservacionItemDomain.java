package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionObservacionItemDomain {

    private ObservacionItemDomain observacionItem;
    private UUID asesorFicha;

    private AgregacionObservacionItemDomain() {}

    public static AgregacionObservacionItemDomain crear(ObservacionItemDomain observacionItem, UUID asesorFicha) {
        var agregacion = new AgregacionObservacionItemDomain();
        var result = new ValidationResult();

        agregacion.setObservacionItem(observacionItem, result);
        agregacion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setObservacionItem(ObservacionItemDomain observacionItem, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionItem,
                FichasFields.ObservacionItem.OBSERVACION_ITEM,
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO, result)) {
            return;
        }
        this.observacionItem = observacionItem;
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionItem.ASESOR_FICHA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public ObservacionItemDomain getObservacionItem() {
        return observacionItem;
    }

    public UUID getRevisionItem() {
        return observacionItem.getRevisionItem();
    }

    public String getObservacion() {
        return observacionItem.getObservacion();
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
