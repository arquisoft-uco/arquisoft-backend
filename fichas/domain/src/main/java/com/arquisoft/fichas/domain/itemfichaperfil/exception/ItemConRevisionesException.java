package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;

import java.util.UUID;

public final class ItemConRevisionesException extends DomainException {

    public ItemConRevisionesException(UUID item) {
        super(BaseError.conDetalle(
                FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES,
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_CON_REVISIONES),
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_CON_REVISIONES_DETALLE, item)
        ));
    }
}
