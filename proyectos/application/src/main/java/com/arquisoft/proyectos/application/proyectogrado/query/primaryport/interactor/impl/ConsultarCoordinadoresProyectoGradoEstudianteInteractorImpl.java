package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarCoordinadoresProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper.ConsultarCoordinadoresProyectoGradoEstudianteMapper;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarCoordinadoresProyectoGradoEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresProyectoGradoEstudianteInteractorImpl
        implements ConsultarCoordinadoresProyectoGradoEstudianteInteractor {

    private final ConsultarCoordinadoresProyectoGradoEstudianteUseCase useCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<CoordinadorProyectoGradoReadModel> ejecutar(
            ConsultarCoordinadoresProyectoGradoEstudianteQuery entrada) {
        var criteria = ConsultarCoordinadoresProyectoGradoEstudianteMapper.toCriteria(entrada);
        return useCase.ejecutar(criteria);
    }
}
