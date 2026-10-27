package com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;

import java.util.List;
import java.util.UUID;

public final class VersionRepositorioArtefactoNoEncontradaException extends DomainException {

    public VersionRepositorioArtefactoNoEncontradaException(List<UUID> versiones) {
        super(
                Mensajes.formatear(AgregarVersionRepositorioArtefactoKey.ERROR_NO_ENCONTRADAS, versiones),
                ArtefactosCodes.VersionRepositorioArtefacto.VERSIONES_NO_ENCONTRADAS
        );
    }
}
