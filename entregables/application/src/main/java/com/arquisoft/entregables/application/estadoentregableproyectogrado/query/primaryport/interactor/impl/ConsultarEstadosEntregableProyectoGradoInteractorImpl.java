package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor.ConsultarEstadosEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.mapper.ConsultarEstadosEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase.ConsultarEstadosEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEntregableProyectoGradoInteractorImpl
        implements ConsultarEstadosEntregableProyectoGradoInteractor {

    private final ConsultarEstadosEntregableProyectoGradoUseCase consultarEstadosEntregableProyectoGradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public List<EstadoEntregableProyectoGradoReadModel> ejecutar(ConsultarEstadosEntregableProyectoGradoQuery entrada) {
        var criteria = ConsultarEstadosEntregableProyectoGradoMapper.toCriteria(entrada);
        return consultarEstadosEntregableProyectoGradoUseCase.ejecutar(criteria);
    }
}
