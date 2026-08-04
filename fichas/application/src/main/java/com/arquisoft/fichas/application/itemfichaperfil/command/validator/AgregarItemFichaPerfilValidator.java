package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;

import java.util.UUID;

public interface AgregarItemFichaPerfilValidator {

    void validar(ItemFichaPerfilAggregate item, UUID estudiante);
}
