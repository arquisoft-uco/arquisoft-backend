package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;

import java.util.UUID;

public final class EstadoFichaPerfilNoEncontradoException extends ApplicationException {

    public EstadoFichaPerfilNoEncontradoException(UUID fichaPerfilId) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_NO_ENCONTRADO, fichaPerfilId),
                FichasCodes.EstadoFichaPerfil.NO_ENCONTRADO
        );
    }
}
