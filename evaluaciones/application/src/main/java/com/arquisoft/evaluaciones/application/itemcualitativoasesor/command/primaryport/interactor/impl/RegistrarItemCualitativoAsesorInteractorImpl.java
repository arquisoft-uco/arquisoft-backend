package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.RegistrarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper.RegistrarItemCualitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RegistrarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RegistrarItemCualitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCualitativoAsesorInteractorImpl
        implements RegistrarItemCualitativoAsesorInteractor {

    private final RegistrarItemCualitativoAsesorUseCase registrarItemCualitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarItemCualitativoAsesorCommand command) {
        return registrarItemCualitativoAsesorUseCase.ejecutar(
                RegistrarItemCualitativoAsesorMapper.toDomain(command));
    }
}
