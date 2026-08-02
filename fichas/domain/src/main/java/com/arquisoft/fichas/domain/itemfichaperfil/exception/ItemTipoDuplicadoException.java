package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

public final class ItemTipoDuplicadoException extends ApplicationException {

    public ItemTipoDuplicadoException(String tipoItem) {
        super(
                Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_TIPO_DUPLICADO, tipoItem),
                FichasCodes.ItemFichaPerfil.ITEM_TIPO_DUPLICADO
        );
    }
}
