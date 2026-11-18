package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresActualesProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper.ConsultarAsesoresActualesProyectoGradoEstudianteMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresActualesProyectoGradoEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresActualesProyectoGradoEstudianteInteractorImpl
        implements ConsultarAsesoresActualesProyectoGradoEstudianteInteractor {

    private final ConsultarAsesoresActualesProyectoGradoEstudianteUseCase useCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<AsesorActualProyectoGradoReadModel> ejecutar(
            ConsultarAsesoresActualesProyectoGradoEstudianteQuery entrada) {
        var criteria = ConsultarAsesoresActualesProyectoGradoEstudianteMapper.toCriteria(entrada);
        return useCase.ejecutar(criteria);
    }
}
