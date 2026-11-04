package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarDetalleProyectoGradoAsesorInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper.ConsultarDetalleProyectoGradoAsesorMapper;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarDetalleProyectoGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarDetalleProyectoGradoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarDetalleProyectoGradoAsesorInteractorImpl
        implements ConsultarDetalleProyectoGradoAsesorInteractor {

    private final ConsultarDetalleProyectoGradoAsesorUseCase consultarDetalleProyectoGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public ProyectoGradoReadModel ejecutar(ConsultarDetalleProyectoGradoAsesorQuery entrada) {
        var criteria = ConsultarDetalleProyectoGradoAsesorMapper.toCriteria(entrada);
        return consultarDetalleProyectoGradoAsesorUseCase.ejecutar(criteria);
    }
}
