package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresProyectoGradoCoordinadorInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper.ConsultarAsesoresProyectoGradoCoordinadorMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresProyectoGradoCoordinadorInteractorImpl
        implements ConsultarAsesoresProyectoGradoCoordinadorInteractor {

    private final ConsultarAsesoresProyectoGradoCoordinadorUseCase consultarAsesoresProyectoGradoCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<AsesorProyectoGradoReadModel> ejecutar(ConsultarAsesoresProyectoGradoCoordinadorQuery entrada) {
        var criteria = ConsultarAsesoresProyectoGradoCoordinadorMapper.toCriteria(entrada);
        return consultarAsesoresProyectoGradoCoordinadorUseCase.ejecutar(criteria);
    }
}
