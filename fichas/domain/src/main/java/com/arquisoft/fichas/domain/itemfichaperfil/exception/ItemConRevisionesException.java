package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;

import java.util.UUID;

public final class ItemConRevisionesException extends DomainException {

    public ItemConRevisionesException(UUID item) {
        super(
                Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_CON_REVISIONES, item),
                FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES
        );
    }
}
