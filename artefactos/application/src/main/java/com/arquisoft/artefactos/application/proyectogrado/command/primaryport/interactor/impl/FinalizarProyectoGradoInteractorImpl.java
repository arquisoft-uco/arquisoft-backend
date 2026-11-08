package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.FinalizarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.FinalizarProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.FinalizarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.FinalizarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FinalizarProyectoGradoInteractorImpl implements FinalizarProyectoGradoInteractor {

    private final FinalizarProyectoGradoUseCase finalizarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public FinalizacionProyectoGradoResult ejecutar(FinalizarProyectoGradoCommand command) {
        return finalizarProyectoGradoUseCase.ejecutar(FinalizarProyectoGradoMapper.toDomain(command));
    }
}
