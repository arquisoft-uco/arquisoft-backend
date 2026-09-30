package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichasPerfilEstudianteMapper;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilEstudianteInteractorImpl implements ConsultarFichasPerfilEstudianteInteractor {

    private final ConsultarFichasPerfilEstudianteUseCase consultarFichasPerfilEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<FichaPerfilEstudianteReadModel> ejecutar(ConsultarFichasPerfilEstudianteQuery entrada) {
        var criteria = ConsultarFichasPerfilEstudianteMapper.toCriteria(entrada);
        return consultarFichasPerfilEstudianteUseCase.ejecutar(criteria);
    }
}
