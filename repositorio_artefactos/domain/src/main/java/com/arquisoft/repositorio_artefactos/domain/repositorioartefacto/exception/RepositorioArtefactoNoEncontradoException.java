package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;

import java.util.UUID;

public final class RepositorioArtefactoNoEncontradoException extends DomainException {

    public RepositorioArtefactoNoEncontradoException(UUID repositorio) {
        super(
                Mensajes.formatear(RepositorioArtefactoKey.ERROR_NO_ENCONTRADO, repositorio),
                RepositorioArtefactosCodes.RepositorioArtefacto.NO_ENCONTRADO
        );
    }
}
