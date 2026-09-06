package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichaPerfilEstudianteMapper;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichaPerfilEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarFichaPerfilEstudianteInteractorImpl implements ConsultarFichaPerfilEstudianteInteractor {

    private final ConsultarFichaPerfilEstudianteUseCase consultarFichaPerfilEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public Optional<FichaPerfilEstudianteReadModel> ejecutar(ConsultarFichaPerfilEstudianteQuery entrada) {
        var criteria = ConsultarFichaPerfilEstudianteMapper.toCriteria(entrada);
        return consultarFichaPerfilEstudianteUseCase.ejecutar(criteria);
    }
}
