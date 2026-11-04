package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;

import java.util.UUID;

public final class VersionArtefactoConRevisionException extends DomainException {

    public VersionArtefactoConRevisionException(UUID versionArtefacto, int numeroVersion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.CON_REVISION,
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_CON_REVISION, numeroVersion),
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_CON_REVISION_DETALLE,
                        versionArtefacto, numeroVersion)
        ));
    }
}
