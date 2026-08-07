package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenDomain;
import com.arquisoft.shared.interactor.Interactor;

public interface ValidarTokenInteractor
        extends Interactor<TokenDomain, ValidacionTokenResult> {}
