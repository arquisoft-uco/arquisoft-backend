package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.ModificarItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper.ModificarItemCualitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.ModificarItemCualitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemCualitativoJuradoInteractorImpl
        implements ModificarItemCualitativoJuradoInteractor {

    private final ModificarItemCualitativoJuradoUseCase modificarItemCualitativoJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarItemCualitativoJuradoCommand command) {
        modificarItemCualitativoJuradoUseCase.ejecutar(
                ModificarItemCualitativoJuradoMapper.toDomain(command));
    }
}
