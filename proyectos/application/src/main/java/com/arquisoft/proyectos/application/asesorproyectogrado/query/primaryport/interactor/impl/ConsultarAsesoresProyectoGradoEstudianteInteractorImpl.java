package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper.ConsultarAsesoresProyectoGradoEstudianteMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresProyectoGradoEstudianteInteractorImpl
        implements ConsultarAsesoresProyectoGradoEstudianteInteractor {

    private final ConsultarAsesoresProyectoGradoEstudianteUseCase consultarAsesoresProyectoGradoEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<AsesorProyectoGradoReadModel> ejecutar(ConsultarAsesoresProyectoGradoEstudianteQuery entrada) {
        var criteria = ConsultarAsesoresProyectoGradoEstudianteMapper.toCriteria(entrada);
        return consultarAsesoresProyectoGradoEstudianteUseCase.ejecutar(criteria);
    }
}
