package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarUsuariosAdministradorInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.mapper.ConsultarUsuariosAdministradorMapper;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuariosAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarUsuariosAdministradorInteractorImpl implements ConsultarUsuariosAdministradorInteractor {

    private final ConsultarUsuariosAdministradorUseCase consultarUsuariosAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<UsuarioReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarUsuariosAdministradorMapper.toCriteria(entrada);
        return consultarUsuariosAdministradorUseCase.ejecutar(criteria);
    }
}
