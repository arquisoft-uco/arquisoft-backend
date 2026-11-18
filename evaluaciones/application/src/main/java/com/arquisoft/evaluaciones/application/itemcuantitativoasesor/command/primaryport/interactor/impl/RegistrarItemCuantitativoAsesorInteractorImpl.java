package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.RegistrarItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper.RegistrarItemCuantitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RegistrarItemCuantitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCuantitativoAsesorInteractorImpl
        implements RegistrarItemCuantitativoAsesorInteractor {

    private final RegistrarItemCuantitativoAsesorUseCase registrarItemCuantitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarItemCuantitativoAsesorCommand command) {
        return registrarItemCuantitativoAsesorUseCase.ejecutar(
                RegistrarItemCuantitativoAsesorMapper.toDomain(command));
    }
}
