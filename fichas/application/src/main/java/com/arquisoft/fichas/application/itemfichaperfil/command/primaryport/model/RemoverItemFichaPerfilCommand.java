package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverItemFichaPerfilCommand(UUID item, UUID estudiante) {

    public static RemoverItemFichaPerfilCommand crear(UUID item, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM, FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverItemFichaPerfilCommand(item, estudiante);
    }
}
