package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.ConsultarEstadosProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper.ConsultarEstadosProyectoGradoEstudianteMapper;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarEstadosProyectoGradoEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosProyectoGradoEstudianteInteractorImpl implements ConsultarEstadosProyectoGradoEstudianteInteractor {

    private final ConsultarEstadosProyectoGradoEstudianteUseCase consultarEstadosProyectoGradoEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstadoProyectoGradoReadModel> ejecutar(ConsultarEstadosProyectoGradoEstudianteQuery entrada) {
        var criteria = ConsultarEstadosProyectoGradoEstudianteMapper.toCriteria(entrada);
        return consultarEstadosProyectoGradoEstudianteUseCase.ejecutar(criteria);
    }
}
