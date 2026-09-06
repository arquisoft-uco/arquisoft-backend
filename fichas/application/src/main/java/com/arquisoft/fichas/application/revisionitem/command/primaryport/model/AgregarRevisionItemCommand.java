package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record AgregarRevisionItemCommand(UUID item, UUID asesorFicha) {

    public static AgregarRevisionItemCommand crear(UUID item, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(item,
                FichasFields.RevisionItem.ITEM,
                FichasCodes.RevisionItem.ITEM_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarRevisionItemCommand(item, asesorFicha);
    }
}
