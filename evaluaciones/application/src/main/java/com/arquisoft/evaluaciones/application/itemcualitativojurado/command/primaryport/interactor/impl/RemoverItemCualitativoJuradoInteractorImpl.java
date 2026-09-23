package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.RemoverItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper.RemoverItemCualitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RemoverItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RemoverItemCualitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemCualitativoJuradoInteractorImpl
        implements RemoverItemCualitativoJuradoInteractor {

    private final RemoverItemCualitativoJuradoUseCase removerItemCualitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverItemCualitativoJuradoCommand command) {
        removerItemCualitativoJuradoUseCase.ejecutar(
                RemoverItemCualitativoJuradoMapper.toDomain(command));
    }
}
