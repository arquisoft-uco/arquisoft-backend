package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public record ModificarItemFichaPerfilCommand(
        UUID item,
        String contenido,
        UUID estudiante
) {

    public ModificarItemFichaPerfilCommand {
        contenido = UtilText.applyTrim(contenido);
    }

    public static ModificarItemFichaPerfilCommand crear(UUID item, String contenido, UUID estudiante) {
        var result = new ValidationResult();

        DomainValidator.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM, FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result);

        if (DomainValidator.noEnBlanco(contenido,
                FichasFields.ItemFichaPerfil.CONTENIDO,
                FichasCodes.ItemFichaPerfil.CONTENIDO_REQUERIDO, result)) {
            DomainValidator.longitudMaxima(contenido, FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                    FichasFields.ItemFichaPerfil.CONTENIDO,
                    FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO, result);
        }

        DomainValidator.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarItemFichaPerfilCommand(item, contenido, estudiante);
    }
}
