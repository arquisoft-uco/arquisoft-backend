package com.arquisoft.fichas.application.itemfichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class ItemFichaPerfilNoEncontradoException extends ApplicationException {

    public ItemFichaPerfilNoEncontradoException(UUID itemId) {
        super(Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_NO_ENCONTRADO, itemId),
                FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO);
    }
}
