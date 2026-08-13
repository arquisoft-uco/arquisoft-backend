package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import java.util.UUID;

public interface RemoverItemFichaPerfilValidator {

    void validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste,
                 boolean esPropietario, long totalRevisiones);
}
