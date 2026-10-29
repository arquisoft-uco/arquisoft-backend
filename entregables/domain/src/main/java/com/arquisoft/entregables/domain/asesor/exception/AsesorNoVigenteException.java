package com.arquisoft.entregables.domain.asesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.AsesorKey;

import java.util.UUID;

public final class AsesorNoVigenteException extends DomainException {

    public AsesorNoVigenteException(UUID asesor) {
        super(BaseError.conDetalle(
                EntregablesCodes.Asesor.NO_VIGENTE,
                Mensajes.formatear(AsesorKey.ERROR_NO_VIGENTE),
                Mensajes.formatear(AsesorKey.ERROR_NO_VIGENTE_DETALLE, asesor)
        ));
    }
}
