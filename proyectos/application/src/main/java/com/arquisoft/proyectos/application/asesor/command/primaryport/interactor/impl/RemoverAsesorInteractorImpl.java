package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.mapper.RemoverAsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.RemoverAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAsesorInteractorImpl implements RemoverAsesorInteractor {

    private final RemoverAsesorUseCase removerAsesorUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public RemocionAsesorResult ejecutar(RemoverAsesorCommand command) {
        return removerAsesorUseCase.ejecutar(RemoverAsesorMapper.toDomain(command));
    }
}
