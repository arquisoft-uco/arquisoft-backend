package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import java.util.UUID;

public interface RemoverItemFichaPerfilValidator {

    void validar(UUID fichaPerfil, UUID estudiante);
}
