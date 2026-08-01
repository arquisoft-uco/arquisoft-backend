package com.arquisoft.fichas.application.estadoficha.query;

import com.arquisoft.fichas.application.estadoficha.query.port.in.ConsultarEstadosFichaUseCase;
import com.arquisoft.fichas.application.estadoficha.query.port.out.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
public class ConsultarEstadosFichaUseCaseImpl implements ConsultarEstadosFichaUseCase {

    private final EstadoFichaQueryOutputPort queryOutputPort;

    @Override
    public List<EstadoFichaReadModel> ejecutar(Void entrada) {
        return queryOutputPort.findAll();
    }
}
