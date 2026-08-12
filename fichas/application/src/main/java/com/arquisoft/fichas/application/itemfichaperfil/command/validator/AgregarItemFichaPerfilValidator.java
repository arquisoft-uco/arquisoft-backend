package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;

import java.util.UUID;

public interface AgregarItemFichaPerfilValidator {

    void validar(ItemFichaPerfilDomain item, UUID estudiante, boolean fichaExiste, boolean esPropietario,
                 boolean tipoYaExiste);
}
