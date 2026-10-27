package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilNumero;

public final class TamanoArchivoExcedidoException extends DomainException {

    public TamanoArchivoExcedidoException(String nombreArchivo, long tamanoBytes, long maximoBytes) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.TAMANO_ARCHIVO_EXCEDIDO,
                Mensajes.formatear(VersionArtefactoKey.ERROR_TAMANO_EXCEDIDO,
                        nombreArchivo, UtilNumero.formatearMegabytes(maximoBytes)),
                Mensajes.formatear(VersionArtefactoKey.ERROR_TAMANO_EXCEDIDO_DETALLE, tamanoBytes, maximoBytes)
        ));
    }
}
