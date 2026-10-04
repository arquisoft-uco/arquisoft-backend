package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record MarcarRevisionItemComoVisualizadaCommand(UUID revisionItem, UUID estudiante) {

    public static MarcarRevisionItemComoVisualizadaCommand crear(UUID revisionItem, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(revisionItem,
                FichasFields.RevisionItem.REVISION_ITEM,
                FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.RevisionItem.ESTUDIANTE,
                FichasCodes.RevisionItem.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new MarcarRevisionItemComoVisualizadaCommand(revisionItem, estudiante);
    }
}
