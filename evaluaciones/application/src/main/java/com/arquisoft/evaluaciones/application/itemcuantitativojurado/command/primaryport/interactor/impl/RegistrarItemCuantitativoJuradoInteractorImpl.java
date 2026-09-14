package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.RegistrarItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper.RegistrarItemCuantitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RegistrarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RegistrarItemCuantitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCuantitativoJuradoInteractorImpl
        implements RegistrarItemCuantitativoJuradoInteractor {

    private final RegistrarItemCuantitativoJuradoUseCase registrarItemCuantitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarItemCuantitativoJuradoCommand command) {
        return registrarItemCuantitativoJuradoUseCase.ejecutar(
                RegistrarItemCuantitativoJuradoMapper.toDomain(command));
    }
}
