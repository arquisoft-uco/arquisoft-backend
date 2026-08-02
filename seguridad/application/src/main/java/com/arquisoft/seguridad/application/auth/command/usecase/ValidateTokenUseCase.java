package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.shared.usecase.UseCase;

public interface ValidateTokenUseCase extends UseCase<TokenAggregate, ValidacionTokenResult> {}
