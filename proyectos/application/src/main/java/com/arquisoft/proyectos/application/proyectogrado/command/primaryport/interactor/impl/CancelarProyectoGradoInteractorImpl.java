package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper.CancelarProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelarProyectoGradoInteractorImpl implements CancelarProyectoGradoInteractor {

    private final CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public void ejecutar(CancelarProyectoGradoCommand command) {
        cancelarProyectoGradoUseCase.ejecutar(CancelarProyectoGradoMapper.toDomain(command));
    }
}
