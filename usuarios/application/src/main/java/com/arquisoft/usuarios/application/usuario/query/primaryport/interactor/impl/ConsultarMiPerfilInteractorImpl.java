package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarMiPerfilInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.mapper.ConsultarMiPerfilMapper;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarMiPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarMiPerfilInteractorImpl implements ConsultarMiPerfilInteractor {

    private final ConsultarMiPerfilUseCase consultarMiPerfilUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PerfilUsuarioReadModel ejecutar(ConsultarMiPerfilQuery entrada) {
        var criteria = ConsultarMiPerfilMapper.toCriteria(entrada);
        return consultarMiPerfilUseCase.ejecutar(criteria);
    }
}
