package com.arquisoft.fichas.application.itemfichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class ItemFichaPerfilNoEncontradoException extends ApplicationException {

    public ItemFichaPerfilNoEncontradoException(UUID itemId) {
        super(FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO_MSG.formatted(itemId),
                FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO);
    }
}
