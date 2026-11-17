package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;

import java.util.UUID;

public final class DocumentacionYaRegistradaException extends DomainException {

    public DocumentacionYaRegistradaException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Documentacion.YA_REGISTRADA,
                Mensajes.obtener(DocumentacionKey.ERROR_YA_REGISTRADA),
                Mensajes.formatear(DocumentacionKey.ERROR_YA_REGISTRADA_DETALLE, entregableProyectoGrado)
        ));
    }
}
