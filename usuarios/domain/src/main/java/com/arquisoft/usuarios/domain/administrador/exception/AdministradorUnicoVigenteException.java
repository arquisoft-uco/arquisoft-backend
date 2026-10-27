package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAdministradorKey;

import java.util.UUID;

public final class AdministradorUnicoVigenteException extends DomainException {

    public AdministradorUnicoVigenteException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Administrador.ADMINISTRADOR_UNICO_VIGENTE,
                Mensajes.formatear(RemoverAdministradorKey.ERROR_UNICO_VIGENTE),
                Mensajes.formatear(RemoverAdministradorKey.ERROR_UNICO_VIGENTE_DETALLE, usuario)
        ));
    }
}
