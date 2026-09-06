package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarRevisionItemCommand(UUID item, String estadoRevision, UUID asesorFicha) {

    public ModificarRevisionItemCommand {
        estadoRevision = UtilTexto.aplicarTrim(estadoRevision);
    }

    public static ModificarRevisionItemCommand crear(UUID item, String estadoRevision, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(item,
                FichasFields.RevisionItem.ITEM,
                FichasCodes.RevisionItem.ITEM_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(estadoRevision,
                FichasFields.RevisionItem.ESTADO_REVISION,
                FichasCodes.RevisionItem.ESTADO_REVISION_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(estadoRevision, FichasLimits.RevisionItem.ESTADO_MAX,
                    FichasFields.RevisionItem.ESTADO_REVISION,
                    FichasCodes.RevisionItem.ESTADO_REVISION_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.RevisionItem.ASESOR_FICHA,
                FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarRevisionItemCommand(item, estadoRevision, asesorFicha);
    }
}
