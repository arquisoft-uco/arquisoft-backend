package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class DocumentacionNoCompartibleException extends DomainException {

    public DocumentacionNoCompartibleException(UUID documentacion) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.NO_COMPARTIBLE,
                Mensajes.formatear(DocumentacionKey.ERROR_NO_COMPARTIBLE),
                Mensajes.formatear(DocumentacionKey.ERROR_NO_COMPARTIBLE_DETALLE, documentacion)
        ));
    }
}
