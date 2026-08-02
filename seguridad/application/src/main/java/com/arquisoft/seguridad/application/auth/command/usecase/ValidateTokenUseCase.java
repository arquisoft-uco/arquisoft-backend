package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.shared.usecase.UseCase;

public interface ValidateTokenUseCase
        extends UseCase<TokenAggregate, ValidateTokenUseCase.ValidationResult> {

    record ValidationResult(
            boolean valido,
            String identidadId,
            String correo,
            String mensaje
    ) {}
}
