package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.RegistrarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper.RegistrarEvaluacionesCuantitativasJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.RegistrarEvaluacionesCuantitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCuantitativasJuradoInteractorImpl
        implements RegistrarEvaluacionesCuantitativasJuradoInteractor {

    private final RegistrarEvaluacionesCuantitativasJuradoUseCase registrarEvaluacionesCuantitativasJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RegistrarEvaluacionesCuantitativasJuradoCommand command) {
        registrarEvaluacionesCuantitativasJuradoUseCase.ejecutar(
                RegistrarEvaluacionesCuantitativasJuradoMapper.toDomain(command));
    }
}
