package com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionRepositorioArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioArtefactoEnUsoException extends DomainException {

    public VersionRepositorioArtefactoEnUsoException(String nombreDocumento, int numeroVersion, UUID version) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionRepositorioArtefacto.EN_USO,
                Mensajes.formatear(EliminarVersionRepositorioArtefactoKey.ERROR_EN_USO, numeroVersion, nombreDocumento),
                Mensajes.formatear(EliminarVersionRepositorioArtefactoKey.ERROR_EN_USO_DETALLE, version)
        ));
    }
}
