package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.ConsultarCompanerosFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper.ConsultarCompanerosFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarCompanerosFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCompanerosFichaPerfilInteractorImpl implements ConsultarCompanerosFichaPerfilInteractor {

    private final ConsultarCompanerosFichaPerfilUseCase consultarCompanerosFichaPerfilUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstudianteFichaPerfilReadModel> ejecutar(ConsultarCompanerosFichaPerfilQuery entrada) {
        var criteria = ConsultarCompanerosFichaPerfilMapper.toCriteria(entrada);
        return consultarCompanerosFichaPerfilUseCase.ejecutar(criteria);
    }
}
