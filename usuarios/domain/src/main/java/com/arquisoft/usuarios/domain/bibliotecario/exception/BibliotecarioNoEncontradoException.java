package com.arquisoft.usuarios.domain.bibliotecario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverBibliotecarioKey;

import java.util.UUID;

public final class BibliotecarioNoEncontradoException extends DomainException {

    public BibliotecarioNoEncontradoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Bibliotecario.BIBLIOTECARIO_NO_ENCONTRADO,
                Mensajes.formatear(RemoverBibliotecarioKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(RemoverBibliotecarioKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
