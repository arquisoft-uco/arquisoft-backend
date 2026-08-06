package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

public final class ItemTipoDuplicadoException extends ApplicationException {

    public ItemTipoDuplicadoException(String tipoItem) {
        super(
                Messages.formatear(ItemFichaPerfilKey.ERROR_TIPO_DUPLICADO, tipoItem),
                FichasCodes.ItemFichaPerfil.ITEM_TIPO_DUPLICADO
        );
    }
}
