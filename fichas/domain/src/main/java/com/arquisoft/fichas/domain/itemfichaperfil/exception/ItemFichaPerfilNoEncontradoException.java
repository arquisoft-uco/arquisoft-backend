package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;

import java.util.UUID;

public final class ItemFichaPerfilNoEncontradoException extends DomainException {

    public ItemFichaPerfilNoEncontradoException(UUID itemId) {
        super(Mensajes.formatear(ItemFichaPerfilKey.ERROR_NO_ENCONTRADO, itemId),
                FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO);
    }
}
