package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class CambioVersionRepositorioNoPermitidoException extends DomainException {

    public CambioVersionRepositorioNoPermitidoException(String documento, UUID artefacto, String estadoRevision) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO,
                Mensajes.formatear(ArtefactoKey.ERROR_CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO, documento),
                Mensajes.formatear(ArtefactoKey.ERROR_CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO_DETALLE,
                        artefacto, estadoRevision)
        ));
    }
}
