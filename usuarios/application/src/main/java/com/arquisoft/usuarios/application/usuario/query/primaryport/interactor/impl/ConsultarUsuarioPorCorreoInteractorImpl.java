package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarUsuarioPorCorreoInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.mapper.ConsultarUsuarioPorCorreoMapper;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarUsuarioPorCorreoQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuarioPorCorreoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarUsuarioPorCorreoInteractorImpl implements ConsultarUsuarioPorCorreoInteractor {

    private final ConsultarUsuarioPorCorreoUseCase consultarUsuarioPorCorreoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public UsuarioPorCorreoReadModel ejecutar(ConsultarUsuarioPorCorreoQuery entrada) {
        var criteria = ConsultarUsuarioPorCorreoMapper.toCriteria(entrada);
        return consultarUsuarioPorCorreoUseCase.ejecutar(criteria);
    }
}
