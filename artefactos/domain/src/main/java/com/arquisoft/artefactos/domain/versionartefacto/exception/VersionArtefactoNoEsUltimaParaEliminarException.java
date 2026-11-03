package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;

import java.util.UUID;

public final class VersionArtefactoNoEsUltimaParaEliminarException extends DomainException {

    public VersionArtefactoNoEsUltimaParaEliminarException(UUID versionArtefacto, int numeroVersion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.NO_ES_ULTIMA_PARA_ELIMINAR,
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_NO_ES_ULTIMA, numeroVersion),
                Mensajes.formatear(EliminarVersionArtefactoKey.ERROR_NO_ES_ULTIMA_DETALLE,
                        versionArtefacto, numeroVersion)
        ));
    }
}
