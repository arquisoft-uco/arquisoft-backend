package com.arquisoft.biblioteca.domain.bibliotecario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;

import java.util.UUID;

public final class BibliotecarioNoVigenteException extends DomainException {

    public BibliotecarioNoVigenteException(UUID bibliotecario) {
        super(BaseError.conDetalle(
                BibliotecaCodes.Bibliotecario.NO_VIGENTE,
                Mensajes.obtener(BibliotecarioKey.ERROR_NO_VIGENTE),
                Mensajes.formatear(BibliotecarioKey.ERROR_NO_VIGENTE_DETALLE, bibliotecario)
        ));
    }
}
