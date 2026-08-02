package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.exception.AuthorizationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class ItemFichaNoPropiaException extends AuthorizationException {

    public ItemFichaNoPropiaException(UUID fichaPerfilId) {
        super(
                FichasMessages.ItemFichaPerfil.FICHA_NO_AUTORIZADA_MSG.formatted(fichaPerfilId),
                FichasMessages.ItemFichaPerfil.ITEM_FICHA_NO_AUTORIZADA
        );
    }
}
