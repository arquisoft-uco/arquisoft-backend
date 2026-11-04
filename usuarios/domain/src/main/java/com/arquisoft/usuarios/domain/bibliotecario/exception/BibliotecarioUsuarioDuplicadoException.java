package com.arquisoft.usuarios.domain.bibliotecario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarBibliotecarioKey;

import java.util.UUID;

public final class BibliotecarioUsuarioDuplicadoException extends DomainException {

    public BibliotecarioUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Bibliotecario.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarBibliotecarioKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarBibliotecarioKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
