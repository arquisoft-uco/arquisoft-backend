package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.AgregarProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoInteractorImpl implements AgregarProyectoGradoInteractor {

    private final AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public AgregacionProyectoGradoResult ejecutar(AgregarProyectoGradoCommand command) {
        return agregarProyectoGradoUseCase.ejecutar(AgregarProyectoGradoMapper.toDomain(command));
    }
}
