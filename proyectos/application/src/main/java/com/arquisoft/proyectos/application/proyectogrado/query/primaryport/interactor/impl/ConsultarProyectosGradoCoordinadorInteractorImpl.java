package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoCoordinadorInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper.ConsultarProyectosGradoCoordinadorMapper;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoCoordinadorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoCoordinadorInteractorImpl implements ConsultarProyectosGradoCoordinadorInteractor {

    private final ConsultarProyectosGradoCoordinadorUseCase consultarProyectosGradoCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public PaginatedResult<ProyectoGradoReadModel> ejecutar(ConsultarProyectosGradoCoordinadorQuery entrada) {
        var criteria = ConsultarProyectosGradoCoordinadorMapper.toCriteria(entrada);
        return consultarProyectosGradoCoordinadorUseCase.ejecutar(criteria);
    }
}
