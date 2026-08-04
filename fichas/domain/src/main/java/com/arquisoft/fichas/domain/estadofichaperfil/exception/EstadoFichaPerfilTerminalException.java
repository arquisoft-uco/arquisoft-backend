package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;

public final class EstadoFichaPerfilTerminalException extends DomainException {

    public EstadoFichaPerfilTerminalException(EstadoFicha estadoActual) {
        super(
                Messages.formatear(FichasKeys.EstadoFichaPerfil.ERROR_ESTADO_TERMINAL, estadoActual),
                FichasCodes.EstadoFichaPerfil.ESTADO_TERMINAL
        );
    }
}
