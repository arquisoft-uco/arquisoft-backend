package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarItemFichaPerfilCommand(
        UUID item,
        String contenido,
        UUID estudiante
) {

    public ModificarItemFichaPerfilCommand {
        contenido = UtilTexto.aplicarTrim(contenido);
    }

    public static ModificarItemFichaPerfilCommand crear(UUID item, String contenido, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM, FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(contenido, FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                    FichasFields.ItemFichaPerfil.CONTENIDO,
                    FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarItemFichaPerfilCommand(item, contenido, estudiante);
    }
}
