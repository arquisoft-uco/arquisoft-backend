package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.AuthenticateUserUseCase;
import com.arquisoft.shared.interactor.Interactor;

public interface AuthenticateUserInteractor
        extends Interactor<AuthenticateUserCommand, AuthenticateUserUseCase.AuthResult> {}
