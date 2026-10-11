package com.arquisoft.fichas.application.observacionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverObservacionItemCommand(UUID observacionItem, UUID asesorFicha) {

    public static RemoverObservacionItemCommand crear(UUID observacionItem, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacionItem,
                FichasFields.ObservacionItem.OBSERVACION_ITEM,
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionItem.ASESOR_FICHA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverObservacionItemCommand(observacionItem, asesorFicha);
    }
}
