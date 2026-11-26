package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

public final class FichaPerfilNoEnConstruccionException extends DomainException {

    public FichaPerfilNoEnConstruccionException(EstadoFicha estadoActual) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_NO_EN_CONSTRUCCION, estadoActual.getNombre()),
                FichasCodes.EstadoFichaPerfil.NO_EN_CONSTRUCCION
        );
    }
}
