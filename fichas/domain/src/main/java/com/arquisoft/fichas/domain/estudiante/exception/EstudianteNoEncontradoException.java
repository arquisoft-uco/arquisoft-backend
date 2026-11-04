package com.arquisoft.fichas.domain.estudiante.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import java.util.UUID;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException(UUID estudianteId) {
        super(BaseError.conDetalle(
                FichasCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO,
                Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADO_DETALLE, estudianteId)
        ));
    }
}
