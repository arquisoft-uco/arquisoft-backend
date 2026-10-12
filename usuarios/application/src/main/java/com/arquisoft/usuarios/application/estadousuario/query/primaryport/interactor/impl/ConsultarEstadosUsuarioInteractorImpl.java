package com.arquisoft.usuarios.application.estadousuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.estadousuario.query.primaryport.interactor.ConsultarEstadosUsuarioInteractor;
import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.application.estadousuario.query.usecase.ConsultarEstadosUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosUsuarioInteractorImpl implements ConsultarEstadosUsuarioInteractor {

    private final ConsultarEstadosUsuarioUseCase consultarEstadosUsuarioUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public List<EstadoUsuarioReadModel> ejecutar() {
        return consultarEstadosUsuarioUseCase.ejecutar();
    }
}
