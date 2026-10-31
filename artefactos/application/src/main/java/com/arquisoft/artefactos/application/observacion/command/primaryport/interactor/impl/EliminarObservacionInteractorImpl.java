package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.EliminarObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.mapper.EliminarObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.EliminarObservacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarObservacionInteractorImpl implements EliminarObservacionInteractor {

    private final EliminarObservacionUseCase eliminarObservacionUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(EliminarObservacionCommand command) {
        eliminarObservacionUseCase.ejecutar(EliminarObservacionMapper.toDomain(command));
    }
}
