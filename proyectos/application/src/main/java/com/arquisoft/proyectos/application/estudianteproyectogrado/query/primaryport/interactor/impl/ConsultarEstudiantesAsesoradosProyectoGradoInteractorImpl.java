package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarEstudiantesAsesoradosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper.ConsultarEstudiantesAsesoradosProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesAsesoradosProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesAsesoradosProyectoGradoInteractorImpl
        implements ConsultarEstudiantesAsesoradosProyectoGradoInteractor {

    private final ConsultarEstudiantesAsesoradosProyectoGradoUseCase consultarEstudiantesAsesoradosUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstudianteAsesoradoProyectoGradoReadModel> ejecutar(
            ConsultarEstudiantesAsesoradosProyectoGradoQuery entrada) {
        var criteria = ConsultarEstudiantesAsesoradosProyectoGradoMapper.toCriteria(entrada);
        return consultarEstudiantesAsesoradosUseCase.ejecutar(criteria);
    }
}
