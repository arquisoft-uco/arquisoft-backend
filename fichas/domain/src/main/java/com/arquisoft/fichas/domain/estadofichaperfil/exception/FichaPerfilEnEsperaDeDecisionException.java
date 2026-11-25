package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class FichaPerfilEnEsperaDeDecisionException extends DomainException {

    public FichaPerfilEnEsperaDeDecisionException(UUID fichaPerfil, String tituloProyecto, long finalizadas) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoFichaPerfil.EN_ESPERA_DE_DECISION,
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_EN_ESPERA_DE_DECISION, tituloProyecto, finalizadas),
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_EN_ESPERA_DE_DECISION_DETALLE, fichaPerfil, finalizadas)
        ));
    }
}
