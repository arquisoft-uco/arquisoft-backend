package com.arquisoft.usuarios.application.bibliotecario.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.bibliotecario.query.primaryport.interactor.ConsultarBibliotecariosAdministradorInteractor;
import com.arquisoft.usuarios.application.bibliotecario.query.primaryport.mapper.ConsultarBibliotecariosAdministradorMapper;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.application.bibliotecario.query.usecase.ConsultarBibliotecariosAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarBibliotecariosAdministradorInteractorImpl implements ConsultarBibliotecariosAdministradorInteractor {

    private final ConsultarBibliotecariosAdministradorUseCase consultarBibliotecariosAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<BibliotecarioReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarBibliotecariosAdministradorMapper.toCriteria(entrada);
        return consultarBibliotecariosAdministradorUseCase.ejecutar(criteria);
    }
}
