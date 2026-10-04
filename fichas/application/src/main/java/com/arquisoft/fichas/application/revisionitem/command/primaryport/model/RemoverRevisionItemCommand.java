package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverRevisionItemCommand(UUID revisionItem, UUID asesorFicha) {

    public static RemoverRevisionItemCommand crear(UUID revisionItem, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(revisionItem,
                FichasFields.RevisionItem.REVISION_ITEM,
                FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverRevisionItemCommand(revisionItem, asesorFicha);
    }
}
