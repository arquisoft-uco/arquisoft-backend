package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.RemoverItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper.RemoverItemCualitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RemoverItemCualitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemCualitativoAsesorInteractorImpl
        implements RemoverItemCualitativoAsesorInteractor {

    private final RemoverItemCualitativoAsesorUseCase removerItemCualitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverItemCualitativoAsesorCommand command) {
        removerItemCualitativoAsesorUseCase.ejecutar(
                RemoverItemCualitativoAsesorMapper.toDomain(command));
    }
}
