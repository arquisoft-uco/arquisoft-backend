package com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.CancelarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper.CancelarProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelarProyectoGradoInteractorImpl implements CancelarProyectoGradoInteractor {

    private final CancelarProyectoGradoUseCase cancelarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public CancelacionProyectoGradoResult ejecutar(CancelarProyectoGradoCommand command) {
        return cancelarProyectoGradoUseCase.ejecutar(CancelarProyectoGradoMapper.toDomain(command));
    }
}
