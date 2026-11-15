package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class EstadoDocumentacionSinCambioException extends DomainException {

    public EstadoDocumentacionSinCambioException(UUID documentacion, EstadoDocumentacion estado) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.ESTADO_SIN_CAMBIO,
                Mensajes.formatear(DocumentacionKey.ERROR_ESTADO_SIN_CAMBIO, estado.getNombre()),
                Mensajes.formatear(DocumentacionKey.ERROR_ESTADO_SIN_CAMBIO_DETALLE, documentacion, estado.getId())
        ));
    }
}
