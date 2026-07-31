package com.arquisoft.fichas.application.estadoevaluacionficha.command;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in.AgregarEstadoEvaluacionFichaInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaInteractor implements AgregarEstadoEvaluacionFichaInputPort {

    private final AgregarEstadoEvaluacionFichaUseCase agregarEstadoEvaluacionFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarEstadoEvaluacionFichaCommand command) {
        return agregarEstadoEvaluacionFichaUseCase.ejecutar(command);
    }
}
