package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor.ActualizarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.mapper.ActualizarEstadoEntregableProyectoGradoMapper;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase.ActualizarEstadoEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarEstadoEntregableProyectoGradoInteractorImpl
        implements ActualizarEstadoEntregableProyectoGradoInteractor {

    private final ActualizarEstadoEntregableProyectoGradoUseCase actualizarEstadoEntregableProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public ActualizacionEstadoEntregableProyectoGradoResult ejecutar(
            ActualizarEstadoEntregableProyectoGradoCommand command) {
        return actualizarEstadoEntregableProyectoGradoUseCase.ejecutar(
                ActualizarEstadoEntregableProyectoGradoMapper.toDomain(command));
    }
}
