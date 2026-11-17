package com.arquisoft.biblioteca.infrastructure.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class ArchivoDocumentacionAusenteException extends InfrastructureException {

    public ArchivoDocumentacionAusenteException(UUID documentacion, String bucket, String claveObjeto) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.ARCHIVO_AUSENTE,
                Mensajes.obtener(DocumentacionKey.ERROR_ARCHIVO_AUSENTE),
                Mensajes.formatear(DocumentacionKey.ERROR_ARCHIVO_AUSENTE_DETALLE, documentacion, bucket, claveObjeto)
        ));
    }
}
