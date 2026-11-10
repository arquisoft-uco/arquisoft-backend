package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.RemoverObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper.RemoverObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RemoverObservacionItemJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemJuradoInteractorImpl implements RemoverObservacionItemJuradoInteractor {

    private final RemoverObservacionItemJuradoUseCase removerObservacionItemJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverObservacionItemJuradoCommand command) {
        removerObservacionItemJuradoUseCase.ejecutar(RemoverObservacionItemJuradoMapper.toDomain(command));
    }
}
