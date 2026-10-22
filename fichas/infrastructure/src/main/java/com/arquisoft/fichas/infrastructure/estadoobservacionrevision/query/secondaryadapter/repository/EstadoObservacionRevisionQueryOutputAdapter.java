package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.secondaryport.EstadoObservacionRevisionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.secondaryadapter.repository.mapper.EstadoObservacionRevisionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoObservacionRevisionQueryOutputAdapter implements EstadoObservacionRevisionQueryOutputPort {

    private final EstadoObservacionRevisionQueryRepository repository;

    @Override
    public List<EstadoObservacionRevisionReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(EstadoObservacionRevisionQueryMapper::toReadModel)
                .toList();
    }
}
