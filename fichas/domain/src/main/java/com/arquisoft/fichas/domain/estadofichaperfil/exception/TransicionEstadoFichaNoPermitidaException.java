package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

public final class TransicionEstadoFichaNoPermitidaException extends DomainException {

    public TransicionEstadoFichaNoPermitidaException(EstadoFicha actual, EstadoFicha nuevo) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_TRANSICION_NO_PERMITIDA,
                        actual.getNombre(), nuevo.getNombre()),
                FichasCodes.EstadoFichaPerfil.TRANSICION_NO_PERMITIDA
        );
    }
}
