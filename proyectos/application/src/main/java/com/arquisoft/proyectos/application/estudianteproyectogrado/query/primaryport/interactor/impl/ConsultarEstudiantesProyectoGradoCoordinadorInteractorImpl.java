package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarEstudiantesProyectoGradoCoordinadorInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper.ConsultarEstudiantesProyectoGradoCoordinadorMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesProyectoGradoCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesProyectoGradoCoordinadorInteractorImpl
        implements ConsultarEstudiantesProyectoGradoCoordinadorInteractor {

    private final ConsultarEstudiantesProyectoGradoCoordinadorUseCase consultarEstudiantesProyectoGradoCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstudianteProyectoGradoReadModel> ejecutar(ConsultarEstudiantesProyectoGradoCoordinadorQuery entrada) {
        var criteria = ConsultarEstudiantesProyectoGradoCoordinadorMapper.toCriteria(entrada);
        return consultarEstudiantesProyectoGradoCoordinadorUseCase.ejecutar(criteria);
    }
}
