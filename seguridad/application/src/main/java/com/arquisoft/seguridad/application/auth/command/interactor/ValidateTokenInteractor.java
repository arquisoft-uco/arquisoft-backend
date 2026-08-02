package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.shared.interactor.Interactor;

public interface ValidateTokenInteractor
        extends Interactor<TokenAggregate, ValidacionTokenResult> {}
