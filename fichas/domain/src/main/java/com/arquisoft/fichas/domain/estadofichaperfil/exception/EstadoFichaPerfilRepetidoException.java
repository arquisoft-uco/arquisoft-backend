package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

public final class EstadoFichaPerfilRepetidoException extends DomainException {

    public EstadoFichaPerfilRepetidoException(EstadoFicha estado) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_ESTADO_REPETIDO, estado.getNombre()),
                FichasCodes.EstadoFichaPerfil.ESTADO_REPETIDO
        );
    }
}
