package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.OmitirEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper.OmitirEvaluacionesCuantitativasJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.OmitirEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.OmitirEvaluacionesCuantitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCuantitativasJuradoInteractorImpl
        implements OmitirEvaluacionesCuantitativasJuradoInteractor {

    private final OmitirEvaluacionesCuantitativasJuradoUseCase omitirEvaluacionesCuantitativasJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(OmitirEvaluacionesCuantitativasJuradoCommand command) {
        omitirEvaluacionesCuantitativasJuradoUseCase.ejecutar(
                OmitirEvaluacionesCuantitativasJuradoMapper.toDomain(command));
    }
}
