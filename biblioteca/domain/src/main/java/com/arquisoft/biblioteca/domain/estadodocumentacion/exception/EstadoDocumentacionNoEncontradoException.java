package com.arquisoft.biblioteca.domain.estadodocumentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EstadoDocumentacionKey;

public final class EstadoDocumentacionNoEncontradoException extends DomainException {

    public EstadoDocumentacionNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EstadoDocumentacion.NO_ENCONTRADO,
                Mensajes.formatear(EstadoDocumentacionKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoDocumentacionKey.ERROR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
