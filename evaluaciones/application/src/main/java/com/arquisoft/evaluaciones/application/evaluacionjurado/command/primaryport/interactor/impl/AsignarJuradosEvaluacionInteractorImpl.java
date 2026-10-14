package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.AsignarJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper.AsignarJuradosEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.AsignarJuradosEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarJuradosEvaluacionInteractorImpl implements AsignarJuradosEvaluacionInteractor {

    private final AsignarJuradosEvaluacionUseCase asignarJuradosEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public List<UUID> ejecutar(AsignarJuradosEvaluacionCommand command) {
        return asignarJuradosEvaluacionUseCase.ejecutar(AsignarJuradosEvaluacionMapper.toDomain(command));
    }
}
