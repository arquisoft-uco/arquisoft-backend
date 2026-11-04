package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public final class AsesorFichaNoEncontradoException extends DomainException {

    public AsesorFichaNoEncontradoException(UUID id) {
        super(BaseError.conDetalle(
                FichasCodes.FichaPerfil.ASESOR_NO_ENCONTRADO,
                Mensajes.formatear(FichaPerfilKey.ERROR_ASESOR_NO_ENCONTRADO),
                Mensajes.formatear(FichaPerfilKey.ERROR_ASESOR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
