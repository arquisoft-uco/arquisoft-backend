package com.arquisoft.evaluaciones.domain.jurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;

import java.util.UUID;

public final class JuradoNoEncontradoException extends DomainException {

    public JuradoNoEncontradoException(UUID jurado) {
        super(
                Mensajes.formatear(JuradoKey.ERROR_NO_ENCONTRADO, jurado),
                EvaluacionesCodes.Jurado.NO_ENCONTRADO
        );
    }
}
