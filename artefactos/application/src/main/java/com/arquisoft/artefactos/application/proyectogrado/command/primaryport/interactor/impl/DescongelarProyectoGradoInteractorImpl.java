package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.DescongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.mapper.DescongelarProyectoGradoMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.DescongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.DescongelarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescongelarProyectoGradoInteractorImpl implements DescongelarProyectoGradoInteractor {

    private final DescongelarProyectoGradoUseCase descongelarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public DescongelacionProyectoGradoResult ejecutar(DescongelarProyectoGradoCommand command) {
        return descongelarProyectoGradoUseCase.ejecutar(DescongelarProyectoGradoMapper.toDomain(command));
    }
}
