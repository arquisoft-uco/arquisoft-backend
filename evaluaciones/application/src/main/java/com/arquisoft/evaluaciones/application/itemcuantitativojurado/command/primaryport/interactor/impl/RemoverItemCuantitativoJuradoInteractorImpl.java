package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.RemoverItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper.RemoverItemCuantitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RemoverItemCuantitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemCuantitativoJuradoInteractorImpl
        implements RemoverItemCuantitativoJuradoInteractor {

    private final RemoverItemCuantitativoJuradoUseCase removerItemCuantitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverItemCuantitativoJuradoCommand command) {
        removerItemCuantitativoJuradoUseCase.ejecutar(
                RemoverItemCuantitativoJuradoMapper.toDomain(command));
    }
}
