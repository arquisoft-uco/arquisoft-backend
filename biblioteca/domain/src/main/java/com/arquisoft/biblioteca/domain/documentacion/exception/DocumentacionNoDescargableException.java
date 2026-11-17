package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class DocumentacionNoDescargableException extends DomainException {

    public DocumentacionNoDescargableException(UUID documentacion) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.NO_DESCARGABLE,
                Mensajes.obtener(DocumentacionKey.ERROR_NO_DESCARGABLE),
                Mensajes.formatear(DocumentacionKey.ERROR_NO_DESCARGABLE_DETALLE, documentacion)
        ));
    }
}
