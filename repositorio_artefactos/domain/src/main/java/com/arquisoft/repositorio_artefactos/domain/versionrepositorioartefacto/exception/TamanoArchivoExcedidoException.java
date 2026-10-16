package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

public final class TamanoArchivoExcedidoException extends DomainException {

    public TamanoArchivoExcedidoException(long tamanoBytes, long maximoBytes) {
        super(
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_TAMANO_EXCEDIDO, tamanoBytes, maximoBytes),
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_ARCHIVO_EXCEDIDO
        );
    }
}
