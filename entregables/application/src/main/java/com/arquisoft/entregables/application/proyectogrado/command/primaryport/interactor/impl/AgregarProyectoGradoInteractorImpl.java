package com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper.AgregarProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.entregables.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoInteractorImpl implements AgregarProyectoGradoInteractor {

    private final AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public AgregacionProyectoGradoResult ejecutar(AgregarProyectoGradoCommand command) {
        return agregarProyectoGradoUseCase.ejecutar(AgregarProyectoGradoMapper.toDomain(command));
    }
}
