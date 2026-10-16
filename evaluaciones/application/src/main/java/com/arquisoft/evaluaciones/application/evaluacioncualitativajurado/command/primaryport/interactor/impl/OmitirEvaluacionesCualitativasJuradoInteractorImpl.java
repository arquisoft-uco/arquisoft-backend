package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.OmitirEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper.OmitirEvaluacionesCualitativasJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.OmitirEvaluacionesCualitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCualitativasJuradoInteractorImpl
        implements OmitirEvaluacionesCualitativasJuradoInteractor {

    private final OmitirEvaluacionesCualitativasJuradoUseCase omitirEvaluacionesCualitativasJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(OmitirEvaluacionesCualitativasJuradoCommand command) {
        omitirEvaluacionesCualitativasJuradoUseCase.ejecutar(
                OmitirEvaluacionesCualitativasJuradoMapper.toDomain(command));
    }
}
