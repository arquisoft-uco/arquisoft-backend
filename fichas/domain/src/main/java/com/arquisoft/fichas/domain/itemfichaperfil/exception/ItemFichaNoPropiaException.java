package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public final class ItemFichaNoPropiaException extends DomainException {

    public ItemFichaNoPropiaException(UUID fichaPerfilId) {
        super(BaseError.conDetalle(
                FichasCodes.ItemFichaPerfil.ITEM_FICHA_NO_AUTORIZADA,
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_FICHA_NO_AUTORIZADA),
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_FICHA_NO_AUTORIZADA_DETALLE, fichaPerfilId)
        ));
    }
}
