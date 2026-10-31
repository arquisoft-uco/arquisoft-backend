package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoAsesorObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.mapper.CambiarEstadoAsesorObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoAsesorObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoAsesorObservacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoAsesorObservacionInteractorImpl implements CambiarEstadoAsesorObservacionInteractor {

    private final CambiarEstadoAsesorObservacionUseCase cambiarEstadoAsesorObservacionUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarEstadoAsesorObservacionCommand command) {
        cambiarEstadoAsesorObservacionUseCase.ejecutar(CambiarEstadoAsesorObservacionMapper.toDomain(command));
    }
}
