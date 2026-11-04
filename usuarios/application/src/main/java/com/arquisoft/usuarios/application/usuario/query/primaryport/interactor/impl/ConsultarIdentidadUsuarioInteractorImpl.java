package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarIdentidadUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.mapper.ConsultarIdentidadUsuarioMapper;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarIdentidadUsuarioQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarIdentidadUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarIdentidadUsuarioInteractorImpl implements ConsultarIdentidadUsuarioInteractor {

    private final ConsultarIdentidadUsuarioUseCase consultarIdentidadUsuarioUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public IdentidadUsuarioReadModel ejecutar(ConsultarIdentidadUsuarioQuery entrada) {
        var criteria = ConsultarIdentidadUsuarioMapper.toCriteria(entrada);
        return consultarIdentidadUsuarioUseCase.ejecutar(criteria);
    }
}
