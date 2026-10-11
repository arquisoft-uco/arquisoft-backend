package com.arquisoft.fichas.application.observacionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record AgregarObservacionItemCommand(UUID revisionItem, String observacion, UUID asesorFicha) {

    public AgregarObservacionItemCommand {
        observacion = UtilTexto.aplicarTrim(observacion);
    }

    public static AgregarObservacionItemCommand crear(UUID revisionItem, String observacion, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(revisionItem,
                FichasFields.ObservacionItem.REVISION_ITEM,
                FichasCodes.ObservacionItem.REVISION_ITEM_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(observacion,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(observacion, FichasLimits.ObservacionItem.OBSERVACION_MAX,
                    FichasFields.ObservacionItem.OBSERVACION,
                    FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA, result);
        }

        ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.ObservacionItem.ASESOR_FICHA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarObservacionItemCommand(revisionItem, observacion, asesorFicha);
    }
}
