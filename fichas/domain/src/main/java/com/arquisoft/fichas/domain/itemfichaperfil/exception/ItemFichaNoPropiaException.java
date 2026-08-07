package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.AuthorizationException;

import java.util.UUID;

public final class ItemFichaNoPropiaException extends AuthorizationException {

    public ItemFichaNoPropiaException(UUID fichaPerfilId) {
        super(
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_FICHA_NO_AUTORIZADA, fichaPerfilId),
                FichasCodes.ItemFichaPerfil.ITEM_FICHA_NO_AUTORIZADA
        );
    }
}
