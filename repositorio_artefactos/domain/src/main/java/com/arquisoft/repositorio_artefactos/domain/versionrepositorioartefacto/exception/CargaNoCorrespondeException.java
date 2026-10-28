package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

import java.util.UUID;

public final class CargaNoCorrespondeException extends DomainException {

    public CargaNoCorrespondeException(UUID carga, String nombreArchivo) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_NO_CORRESPONDE,
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_CARGA_NO_CORRESPONDE, nombreArchivo),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_CARGA_NO_CORRESPONDE_DETALLE, carga)
        ));
    }
}
