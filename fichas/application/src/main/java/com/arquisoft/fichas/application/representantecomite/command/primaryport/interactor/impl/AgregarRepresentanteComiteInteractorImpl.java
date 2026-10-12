package com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.AgregarRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper.AgregarRepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.AgregarRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.usecase.AgregarRepresentanteComiteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarRepresentanteComiteInteractorImpl implements AgregarRepresentanteComiteInteractor {

    private final AgregarRepresentanteComiteUseCase agregarRepresentanteComiteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public AgregacionRepresentanteComiteResult ejecutar(AgregarRepresentanteComiteCommand command) {
        return agregarRepresentanteComiteUseCase.ejecutar(AgregarRepresentanteComiteMapper.toDomain(command));
    }
}
