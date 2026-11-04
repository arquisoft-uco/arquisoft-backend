package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class EstadoFichaPerfilNoEncontradoException extends DomainException {

    public EstadoFichaPerfilNoEncontradoException(UUID fichaPerfil) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoFichaPerfil.NO_ENCONTRADO,
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_NO_ENCONTRADO_DETALLE, fichaPerfil)
        ));
    }
}
