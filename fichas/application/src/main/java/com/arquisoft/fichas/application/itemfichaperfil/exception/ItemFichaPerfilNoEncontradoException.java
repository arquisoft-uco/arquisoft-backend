package com.arquisoft.fichas.application.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;

import java.util.UUID;

public final class ItemFichaPerfilNoEncontradoException extends ApplicationException {

    public ItemFichaPerfilNoEncontradoException(UUID itemId) {
        super(Messages.formatear(ItemFichaPerfilKey.ERROR_NO_ENCONTRADO, itemId),
                FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO);
    }
}
