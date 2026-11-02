package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoEstudianteObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.mapper.CambiarEstadoEstudianteObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoEstudianteObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoEstudianteObservacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEstudianteObservacionInteractorImpl implements CambiarEstadoEstudianteObservacionInteractor {

    private final CambiarEstadoEstudianteObservacionUseCase cambiarEstadoEstudianteObservacionUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarEstadoEstudianteObservacionCommand command) {
        cambiarEstadoEstudianteObservacionUseCase.ejecutar(
                CambiarEstadoEstudianteObservacionMapper.toDomain(command));
    }
}
