package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioInteractorImpl implements RegistrarUsuarioInteractor {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(RegistrarUsuarioCommand input) {
        registrarUsuarioUseCase.ejecutar(input);
    }
}
