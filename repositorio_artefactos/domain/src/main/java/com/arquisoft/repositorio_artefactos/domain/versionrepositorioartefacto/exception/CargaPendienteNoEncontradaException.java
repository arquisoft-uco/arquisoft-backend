package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

import java.util.UUID;

public final class CargaPendienteNoEncontradaException extends DomainException {

    public CargaPendienteNoEncontradaException(UUID carga) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_NO_ENCONTRADA,
                Mensajes.obtener(VersionRepositorioArtefactoKey.ERROR_CARGA_NO_ENCONTRADA),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_CARGA_NO_ENCONTRADA_DETALLE, carga)
        ));
    }
}
