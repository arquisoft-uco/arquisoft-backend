package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilNumero;

public final class TamanoArchivoExcedidoException extends DomainException {

    public TamanoArchivoExcedidoException(String nombreArchivo, long tamanoBytes, long maximoBytes) {
        super(BaseError.conDetalle(
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_ARCHIVO_EXCEDIDO,
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_TAMANO_EXCEDIDO,
                        nombreArchivo, UtilNumero.formatearMegabytes(maximoBytes)),
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_TAMANO_EXCEDIDO_DETALLE, tamanoBytes, maximoBytes)
        ));
    }
}
