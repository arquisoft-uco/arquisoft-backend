package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.CambiarEstadoProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper.CambiarEstadoProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CambiarEstadoProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CambiarEstadoProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoProyectoGradoInteractorImpl implements CambiarEstadoProyectoGradoInteractor {

    private final CambiarEstadoProyectoGradoUseCase cambiarEstadoProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public void ejecutar(CambiarEstadoProyectoGradoCommand command) {
        cambiarEstadoProyectoGradoUseCase.ejecutar(CambiarEstadoProyectoGradoMapper.toDomain(command));
    }
}
