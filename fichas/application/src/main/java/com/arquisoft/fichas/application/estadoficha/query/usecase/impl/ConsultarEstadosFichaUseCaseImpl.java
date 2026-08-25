package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaUseCaseImpl implements ConsultarEstadosFichaUseCase {

    private final EstadoFichaQueryOutputPort queryOutputPort;

    @Override
    public List<EstadoFichaReadModel> ejecutar(Void entrada) {
        return queryOutputPort.findAll();
    }
}
