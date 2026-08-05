package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public record RemoverItemFichaPerfilCommand(UUID item, UUID estudiante) {

    public static RemoverItemFichaPerfilCommand crear(UUID item, UUID estudiante) {
        var result = new ValidationResult();

        DomainValidator.noNulo(item,
                FichasFields.ItemFichaPerfil.ITEM, FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO, result);
        DomainValidator.noNulo(estudiante,
                FichasFields.ItemFichaPerfil.ESTUDIANTE,
                FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverItemFichaPerfilCommand(item, estudiante);
    }
}
