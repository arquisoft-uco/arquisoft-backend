package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoAsesorInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper.ConsultarProyectosGradoAsesorMapper;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoAsesorInteractorImpl implements ConsultarProyectosGradoAsesorInteractor {

    private final ConsultarProyectosGradoAsesorUseCase consultarProyectosGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public PaginatedResult<ProyectoGradoReadModel> ejecutar(ConsultarProyectosGradoAsesorQuery entrada) {
        var criteria = ConsultarProyectosGradoAsesorMapper.toCriteria(entrada);
        return consultarProyectosGradoAsesorUseCase.ejecutar(criteria);
    }
}
