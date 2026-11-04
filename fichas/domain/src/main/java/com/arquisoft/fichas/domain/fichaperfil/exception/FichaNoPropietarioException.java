package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public final class FichaNoPropietarioException extends DomainException {

    public FichaNoPropietarioException(UUID fichaId, UUID estudianteId) {
        super(BaseError.conDetalle(
                FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO,
                Mensajes.formatear(FichaPerfilKey.ERROR_NO_PROPIETARIO),
                Mensajes.formatear(FichaPerfilKey.ERROR_NO_PROPIETARIO_DETALLE, estudianteId, fichaId)
        ));
    }
}
