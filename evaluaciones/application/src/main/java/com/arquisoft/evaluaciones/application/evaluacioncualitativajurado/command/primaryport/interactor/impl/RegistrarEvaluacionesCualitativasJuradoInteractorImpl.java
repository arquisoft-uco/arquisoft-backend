package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.RegistrarEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper.RegistrarEvaluacionesCualitativasJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.RegistrarEvaluacionesCualitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCualitativasJuradoInteractorImpl
        implements RegistrarEvaluacionesCualitativasJuradoInteractor {

    private final RegistrarEvaluacionesCualitativasJuradoUseCase registrarEvaluacionesCualitativasJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RegistrarEvaluacionesCualitativasJuradoCommand command) {
        registrarEvaluacionesCualitativasJuradoUseCase.ejecutar(
                RegistrarEvaluacionesCualitativasJuradoMapper.toDomain(command));
    }
}
