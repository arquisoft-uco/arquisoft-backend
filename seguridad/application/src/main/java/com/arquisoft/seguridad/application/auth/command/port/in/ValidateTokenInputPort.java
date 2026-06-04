package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.shared.inputport.InputPort;

public interface ValidateTokenInputPort
        extends InputPort<TokenAggregate, ValidateTokenInputPort.ValidationResult> {

    record ValidationResult(
            boolean valido,
            String identidadId,
            String correo,
            String mensaje
    ) {}
}
