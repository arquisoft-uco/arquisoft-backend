package com.arquisoft.fichas.application.itemfichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

public final class ItemTipoDuplicadoException extends ApplicationException {

    public ItemTipoDuplicadoException(String tipoItemCode) {
        super(
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_DUPLICADO_MSG.formatted(tipoItemCode),
                FichasMessages.ItemFichaPerfil.ITEM_TIPO_DUPLICADO
        );
    }
}
