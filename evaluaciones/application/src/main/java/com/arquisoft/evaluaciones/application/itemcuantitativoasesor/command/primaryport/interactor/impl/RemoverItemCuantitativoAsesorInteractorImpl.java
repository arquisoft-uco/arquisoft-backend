package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.RemoverItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper.RemoverItemCuantitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RemoverItemCuantitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemCuantitativoAsesorInteractorImpl
        implements RemoverItemCuantitativoAsesorInteractor {

    private final RemoverItemCuantitativoAsesorUseCase removerItemCuantitativoAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverItemCuantitativoAsesorCommand command) {
        removerItemCuantitativoAsesorUseCase.ejecutar(
                RemoverItemCuantitativoAsesorMapper.toDomain(command));
    }
}
