package com.arquisoft.fichas.domain.tipoitem.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;

public final class TipoItemNoEncontradoException extends DomainException {

    public TipoItemNoEncontradoException(String id) {
        super(
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_TIPO_INVALIDO, id),
                FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO
        );
    }
}
