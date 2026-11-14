package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.ConsultarHistorialEstadosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper.ConsultarHistorialEstadosProyectoGradoMapper;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarHistorialEstadosProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarHistorialEstadosProyectoGradoInteractorImpl implements ConsultarHistorialEstadosProyectoGradoInteractor {

    private final ConsultarHistorialEstadosProyectoGradoUseCase consultarHistorialEstadosProyectoGradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstadoProyectoGradoReadModel> ejecutar(ConsultarHistorialEstadosProyectoGradoQuery entrada) {
        var criteria = ConsultarHistorialEstadosProyectoGradoMapper.toCriteria(entrada);
        return consultarHistorialEstadosProyectoGradoUseCase.ejecutar(criteria);
    }
}
