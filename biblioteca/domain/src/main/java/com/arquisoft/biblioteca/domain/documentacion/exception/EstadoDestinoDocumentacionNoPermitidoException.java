package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class EstadoDestinoDocumentacionNoPermitidoException extends DomainException {

    public EstadoDestinoDocumentacionNoPermitidoException(UUID documentacion, EstadoDocumentacion nuevo) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.ESTADO_DESTINO_NO_PERMITIDO,
                Mensajes.formatear(DocumentacionKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, nuevo.getNombre()),
                Mensajes.formatear(DocumentacionKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE,
                        documentacion, nuevo.getId())
        ));
    }
}
