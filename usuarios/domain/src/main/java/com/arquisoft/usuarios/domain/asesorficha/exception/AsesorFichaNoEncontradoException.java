package com.arquisoft.usuarios.domain.asesorficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorFichaKey;

import java.util.UUID;

public final class AsesorFichaNoEncontradoException extends DomainException {

    public AsesorFichaNoEncontradoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.AsesorFicha.ASESOR_FICHA_NO_ENCONTRADO,
                Mensajes.formatear(RemoverAsesorFichaKey.ERROR_NO_ENCONTRADO, nombre),
                Mensajes.formatear(RemoverAsesorFichaKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
