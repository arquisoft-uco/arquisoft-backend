package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper.ConsultarProyectosGradoEstudianteMapper;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoEstudianteInteractorImpl implements ConsultarProyectosGradoEstudianteInteractor {

    private final ConsultarProyectosGradoEstudianteUseCase consultarProyectosGradoEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<ProyectoGradoReadModel> ejecutar(ConsultarProyectosGradoEstudianteQuery entrada) {
        var criteria = ConsultarProyectosGradoEstudianteMapper.toCriteria(entrada);
        return consultarProyectosGradoEstudianteUseCase.ejecutar(criteria);
    }
}
