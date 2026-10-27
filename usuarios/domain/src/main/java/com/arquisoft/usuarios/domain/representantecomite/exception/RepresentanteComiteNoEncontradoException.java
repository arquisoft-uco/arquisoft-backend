package com.arquisoft.usuarios.domain.representantecomite.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverRepresentanteComiteKey;

import java.util.UUID;

public final class RepresentanteComiteNoEncontradoException extends DomainException {

    public RepresentanteComiteNoEncontradoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.RepresentanteComite.REPRESENTANTE_COMITE_NO_ENCONTRADO,
                Mensajes.formatear(RemoverRepresentanteComiteKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(RemoverRepresentanteComiteKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
