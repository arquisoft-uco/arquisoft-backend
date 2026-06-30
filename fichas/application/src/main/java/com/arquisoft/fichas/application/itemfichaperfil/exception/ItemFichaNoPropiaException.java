package com.arquisoft.fichas.application.itemfichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class ItemFichaNoPropiaException extends ApplicationException {

    public ItemFichaNoPropiaException(UUID fichaPerfilId) {
        super(
                FichasMessages.ItemFichaPerfil.FICHA_NO_AUTORIZADA_MSG.formatted(fichaPerfilId),
                FichasMessages.ItemFichaPerfil.ITEM_FICHA_NO_AUTORIZADA
        );
    }
}
