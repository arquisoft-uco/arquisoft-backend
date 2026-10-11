package com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.ActualizarRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper.ActualizarRepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.ActualizarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.usecase.ActualizarRepresentanteComiteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarRepresentanteComiteInteractorImpl implements ActualizarRepresentanteComiteInteractor {

    private final ActualizarRepresentanteComiteUseCase actualizarRepresentanteComiteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public ActualizacionRepresentanteComiteResult ejecutar(ActualizarRepresentanteComiteCommand command) {
        return actualizarRepresentanteComiteUseCase.ejecutar(ActualizarRepresentanteComiteMapper.toDomain(command));
    }
}
