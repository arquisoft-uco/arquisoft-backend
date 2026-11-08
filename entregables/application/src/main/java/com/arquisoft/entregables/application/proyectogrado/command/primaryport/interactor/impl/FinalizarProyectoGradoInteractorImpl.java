package com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper.FinalizarProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.FinalizarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FinalizarProyectoGradoInteractorImpl implements FinalizarProyectoGradoInteractor {

    private final FinalizarProyectoGradoUseCase finalizarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public FinalizacionProyectoGradoResult ejecutar(FinalizarProyectoGradoCommand command) {
        return finalizarProyectoGradoUseCase.ejecutar(FinalizarProyectoGradoMapper.toDomain(command));
    }
}
