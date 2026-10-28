package com.arquisoft.usuarios.domain.asesorficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;

import java.util.UUID;

public final class AsesorFichaUsuarioDuplicadoException extends DomainException {

    public AsesorFichaUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.AsesorFicha.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarAsesorFichaKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarAsesorFichaKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
