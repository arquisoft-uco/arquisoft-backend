package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.ConsultarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper.ConsultarEstudiantesFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarEstudiantesFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesFichaPerfilInteractorImpl implements ConsultarEstudiantesFichaPerfilInteractor {

    private final ConsultarEstudiantesFichaPerfilUseCase consultarEstudiantesFichaPerfilUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstudianteFichaPerfilReadModel> ejecutar(ConsultarEstudiantesFichaPerfilQuery entrada) {
        var criteria = ConsultarEstudiantesFichaPerfilMapper.toCriteria(entrada);
        return consultarEstudiantesFichaPerfilUseCase.ejecutar(criteria);
    }
}
