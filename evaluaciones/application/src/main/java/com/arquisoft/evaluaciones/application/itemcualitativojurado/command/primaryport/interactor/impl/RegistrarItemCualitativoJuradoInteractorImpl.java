package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.RegistrarItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper.RegistrarItemCualitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RegistrarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RegistrarItemCualitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCualitativoJuradoInteractorImpl
        implements RegistrarItemCualitativoJuradoInteractor {

    private final RegistrarItemCualitativoJuradoUseCase registrarItemCualitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarItemCualitativoJuradoCommand command) {
        return registrarItemCualitativoJuradoUseCase.ejecutar(
                RegistrarItemCualitativoJuradoMapper.toDomain(command));
    }
}
