package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.CancelarProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelarProyectoGradoInteractorImpl implements CancelarProyectoGradoInteractor {

    private final CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public CancelacionProyectoGradoResult ejecutar(CancelarProyectoGradoCommand command) {
        return cancelarProyectoGradoUseCase.ejecutar(CancelarProyectoGradoMapper.toDomain(command));
    }
}
