package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioArtefactoNoEncontradaException extends DomainException {

    public VersionRepositorioArtefactoNoEncontradaException(UUID version) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_NO_ENCONTRADA,
                Mensajes.obtener(VersionRepositorioArtefactoKey.ERROR_VERSION_NO_ENCONTRADA),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_VERSION_NO_ENCONTRADA_DETALLE, version)
        ));
    }
}
