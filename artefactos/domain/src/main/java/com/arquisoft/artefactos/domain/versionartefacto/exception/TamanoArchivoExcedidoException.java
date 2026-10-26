package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

public final class TamanoArchivoExcedidoException extends DomainException {

    public TamanoArchivoExcedidoException(long tamanoBytes, long maximoBytes) {
        super(
                Mensajes.formatear(VersionArtefactoKey.ERROR_TAMANO_EXCEDIDO, tamanoBytes, maximoBytes),
                ArtefactosCodes.VersionArtefacto.TAMANO_ARCHIVO_EXCEDIDO
        );
    }
}
