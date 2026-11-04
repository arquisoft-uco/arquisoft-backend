package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

public final class DocumentoYaExisteException extends DomainException {

    public DocumentoYaExisteException(String nombreDocumento) {
        super(
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_DOCUMENTO_YA_EXISTE, nombreDocumento),
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.DOCUMENTO_YA_EXISTE
        );
    }
}
