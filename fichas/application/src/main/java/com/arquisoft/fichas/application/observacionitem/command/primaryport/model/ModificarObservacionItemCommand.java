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

public record ModificarObservacionItemCommand(UUID observacionItem, String observacion, UUID asesorFicha) {

    public ModificarObservacionItemCommand {
        observacion = UtilTexto.aplicarTrim(observacion);
    }

    public static ModificarObservacionItemCommand crear(UUID observacionItem, String observacion, UUID asesorFicha) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacionItem,
                FichasFields.ObservacionItem.OBSERVACION_ITEM,
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO, result);

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

        return new ModificarObservacionItemCommand(observacionItem, observacion, asesorFicha);
    }
}
