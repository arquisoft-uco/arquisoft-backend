package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.GenerarEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper.GenerarEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.GenerarEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenerarEntregableProyectoGradoInteractorImpl implements GenerarEntregableProyectoGradoInteractor {

    private final GenerarEntregableProyectoGradoUseCase generarEntregableProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public UUID ejecutar(GenerarEntregableProyectoGradoCommand command) {
        return generarEntregableProyectoGradoUseCase.ejecutar(GenerarEntregableProyectoGradoMapper.toDomain(command));
    }
}
