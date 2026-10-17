package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;

public final class RepositorioArtefactoDuplicadoException extends DomainException {

    public RepositorioArtefactoDuplicadoException(String nombreRepositorio) {
        super(
                Mensajes.formatear(RepositorioArtefactoKey.ERROR_DUPLICADO, nombreRepositorio),
                RepositorioArtefactosCodes.RepositorioArtefacto.DUPLICADO
        );
    }
}
