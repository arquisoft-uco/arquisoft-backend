package com.arquisoft.artefactos.domain.asesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;

import java.util.UUID;

public final class AsesorNoEncontradoException extends DomainException {

    public AsesorNoEncontradoException(UUID asesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Asesor.NO_ENCONTRADO,
                Mensajes.formatear(AsesorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(AsesorKey.ERROR_NO_ENCONTRADO_DETALLE, asesor)
        ));
    }
}
