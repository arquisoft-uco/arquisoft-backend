package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

import java.util.UUID;

public final class CargaModificadaException extends DomainException {

    public CargaModificadaException(UUID carga, String nombreArchivo) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_MODIFICADA,
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_CARGA_MODIFICADA, nombreArchivo),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_CARGA_MODIFICADA_DETALLE, carga)
        ));
    }
}
