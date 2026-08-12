package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import java.util.Optional;
import java.util.UUID;

public interface RemoverItemFichaPerfilValidator {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void validar(UUID item, UUID estudiante, Optional<UUID> fichaDelItem, boolean esPropietario,
                 long totalRevisiones);
}
