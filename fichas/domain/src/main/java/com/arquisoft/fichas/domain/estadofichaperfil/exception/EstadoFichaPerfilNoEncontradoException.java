package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;

import java.util.UUID;

public final class EstadoFichaPerfilNoEncontradoException extends ApplicationException {

    public EstadoFichaPerfilNoEncontradoException(UUID fichaPerfilId) {
        super(
                Messages.formatear(FichasKeys.EstadoFichaPerfil.ERROR_NO_ENCONTRADO, fichaPerfilId),
                FichasCodes.EstadoFichaPerfil.NO_ENCONTRADO
        );
    }
}
