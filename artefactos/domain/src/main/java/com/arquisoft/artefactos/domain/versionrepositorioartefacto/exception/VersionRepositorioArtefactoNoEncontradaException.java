package com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioArtefactoNoEncontradaException extends DomainException {

    public VersionRepositorioArtefactoNoEncontradaException(UUID version) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionRepositorioArtefacto.VERSION_NO_ENCONTRADA,
                Mensajes.obtener(AgregarVersionRepositorioArtefactoKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(AgregarVersionRepositorioArtefactoKey.ERROR_NO_ENCONTRADA_DETALLE, version)
        ));
    }
}
