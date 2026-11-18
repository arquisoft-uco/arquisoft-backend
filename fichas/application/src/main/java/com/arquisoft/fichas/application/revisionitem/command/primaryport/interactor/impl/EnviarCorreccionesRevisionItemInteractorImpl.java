package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.EnviarCorreccionesRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper.EnviarCorreccionesRevisionItemMapper;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.EnviarCorreccionesRevisionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EnviarCorreccionesRevisionItemInteractorImpl implements EnviarCorreccionesRevisionItemInteractor {

    private final EnviarCorreccionesRevisionItemUseCase enviarCorreccionesRevisionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(EnviarCorreccionesRevisionItemCommand command) {
        enviarCorreccionesRevisionItemUseCase.ejecutar(EnviarCorreccionesRevisionItemMapper.toDomain(command));
    }
}
