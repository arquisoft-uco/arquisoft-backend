package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

public final class FichaPerfilNoDisponibleParaEvaluacionException extends DomainException {

    public FichaPerfilNoDisponibleParaEvaluacionException(EstadoFicha estadoActual) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_NO_DISPONIBLE_PARA_EVALUACION, estadoActual.getNombre()),
                FichasCodes.EstadoFichaPerfil.NO_DISPONIBLE_PARA_EVALUACION
        );
    }
}
