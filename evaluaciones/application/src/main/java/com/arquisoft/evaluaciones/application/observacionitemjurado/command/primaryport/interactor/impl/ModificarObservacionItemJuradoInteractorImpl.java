package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.ModificarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper.ModificarObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.ModificarObservacionItemJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemJuradoInteractorImpl implements ModificarObservacionItemJuradoInteractor {

    private final ModificarObservacionItemJuradoUseCase modificarObservacionItemJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarObservacionItemJuradoCommand command) {
        modificarObservacionItemJuradoUseCase.ejecutar(ModificarObservacionItemJuradoMapper.toDomain(command));
    }
}
