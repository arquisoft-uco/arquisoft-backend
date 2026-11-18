package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.ActualizarEstadoProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.ActualizarEstadoProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarEstadoProyectoGradoInteractorImpl implements ActualizarEstadoProyectoGradoInteractor {

    private final ActualizarEstadoProyectoGradoUseCase actualizarEstadoProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public ActualizacionEstadoProyectoGradoResult ejecutar(ActualizarEstadoProyectoGradoCommand command) {
        return actualizarEstadoProyectoGradoUseCase.ejecutar(
                ActualizarEstadoProyectoGradoMapper.toDomain(command));
    }
}
