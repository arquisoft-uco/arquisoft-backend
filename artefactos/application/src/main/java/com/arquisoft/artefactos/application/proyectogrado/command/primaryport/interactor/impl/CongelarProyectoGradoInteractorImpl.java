package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.CongelarProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CongelarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CongelarProyectoGradoInteractorImpl implements CongelarProyectoGradoInteractor {

    private final CongelarProyectoGradoUseCase congelarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public CongelacionProyectoGradoResult ejecutar(CongelarProyectoGradoCommand command) {
        return congelarProyectoGradoUseCase.ejecutar(CongelarProyectoGradoMapper.toDomain(command));
    }
}
