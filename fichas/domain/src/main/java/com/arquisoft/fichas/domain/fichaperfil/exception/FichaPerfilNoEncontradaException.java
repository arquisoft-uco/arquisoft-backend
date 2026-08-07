package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class FichaPerfilNoEncontradaException extends ApplicationException {

    public FichaPerfilNoEncontradaException(UUID fichaPerfilId) {
        super(
                Mensajes.formatear(FichaPerfilKey.ERROR_NO_ENCONTRADA, fichaPerfilId),
                FichasCodes.FichaPerfil.FICHA_NO_ENCONTRADA
        );
    }
}
