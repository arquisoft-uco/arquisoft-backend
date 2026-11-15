package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class ArchivoDocumentacionNoDisponibleException extends DomainException {

    public ArchivoDocumentacionNoDisponibleException(UUID documentacion) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.ARCHIVO_NO_DISPONIBLE,
                Mensajes.obtener(DocumentacionKey.ERROR_ARCHIVO_NO_DISPONIBLE),
                Mensajes.formatear(DocumentacionKey.ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE, documentacion)
        ));
    }
}
