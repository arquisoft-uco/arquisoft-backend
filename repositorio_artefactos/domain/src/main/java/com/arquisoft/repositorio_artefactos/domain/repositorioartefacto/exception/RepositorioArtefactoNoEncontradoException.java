package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;

import java.util.UUID;

public final class RepositorioArtefactoNoEncontradoException extends DomainException {

    public RepositorioArtefactoNoEncontradoException(UUID repositorio) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.RepositorioArtefacto.NO_ENCONTRADO,
                Mensajes.obtener(RepositorioArtefactoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(RepositorioArtefactoKey.ERROR_NO_ENCONTRADO_DETALLE, repositorio)
        ));
    }
}
