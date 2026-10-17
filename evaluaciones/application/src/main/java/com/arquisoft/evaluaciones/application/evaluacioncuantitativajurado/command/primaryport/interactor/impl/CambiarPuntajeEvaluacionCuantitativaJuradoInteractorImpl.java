package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.CambiarPuntajeEvaluacionCuantitativaJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper.CambiarPuntajeEvaluacionCuantitativaJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.CambiarPuntajeEvaluacionCuantitativaJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarPuntajeEvaluacionCuantitativaJuradoInteractorImpl
        implements CambiarPuntajeEvaluacionCuantitativaJuradoInteractor {

    private final CambiarPuntajeEvaluacionCuantitativaJuradoUseCase cambiarPuntajeEvaluacionCuantitativaJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(CambiarPuntajeEvaluacionCuantitativaJuradoCommand command) {
        cambiarPuntajeEvaluacionCuantitativaJuradoUseCase.ejecutar(
                CambiarPuntajeEvaluacionCuantitativaJuradoMapper.toDomain(command));
    }
}
