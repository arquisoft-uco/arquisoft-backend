package com.arquisoft.fichas.domain.itemfichaperfil.exception;

import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

public final class ItemTipoDuplicadoException extends DomainException {

    public ItemTipoDuplicadoException(TipoItem tipoItem) {
        super(BaseError.conDetalle(
                FichasCodes.ItemFichaPerfil.ITEM_TIPO_DUPLICADO,
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_TIPO_DUPLICADO, tipoItem.getNombre()),
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_TIPO_DUPLICADO_DETALLE, tipoItem.getId())
        ));
    }
}
