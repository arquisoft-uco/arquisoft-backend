package com.arquisoft.biblioteca.infrastructure.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class DocumentacionNoDisponibleException extends InfrastructureException {

    public DocumentacionNoDisponibleException(UUID documentacion) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.NO_DISPONIBLE,
                Mensajes.formatear(DocumentacionKey.ERROR_NO_DISPONIBLE),
                Mensajes.formatear(DocumentacionKey.ERROR_NO_DISPONIBLE_DETALLE, documentacion)
        ));
    }
}
