package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class ActorSinAccesoVersionArtefactoException extends DomainException {

    public ActorSinAccesoVersionArtefactoException(UUID actor, UUID versionArtefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.ACCESO_DENEGADO,
                Mensajes.obtener(VersionArtefactoKey.ERROR_ACCESO_DENEGADO),
                Mensajes.formatear(VersionArtefactoKey.ERROR_ACCESO_DENEGADO_DETALLE, actor, versionArtefacto)
        ));
    }
}
