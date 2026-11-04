package com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionRepositorioArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioArtefactoNoEsUltimaParaEliminarException extends DomainException {

    public VersionRepositorioArtefactoNoEsUltimaParaEliminarException(
            String nombreDocumento, int numeroVersion, int ultimaVersionDocumento, UUID version) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionRepositorioArtefacto.NO_ES_ULTIMA_PARA_ELIMINAR,
                Mensajes.formatear(
                        EliminarVersionRepositorioArtefactoKey.ERROR_NO_ES_ULTIMA,
                        ultimaVersionDocumento, nombreDocumento),
                Mensajes.formatear(
                        EliminarVersionRepositorioArtefactoKey.ERROR_NO_ES_ULTIMA_DETALLE,
                        version, numeroVersion, ultimaVersionDocumento)
        ));
    }
}
