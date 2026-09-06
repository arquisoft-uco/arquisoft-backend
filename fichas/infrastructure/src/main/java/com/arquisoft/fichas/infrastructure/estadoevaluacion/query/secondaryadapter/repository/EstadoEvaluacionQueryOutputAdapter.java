package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository.mapper.EstadoEvaluacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionQueryOutputAdapter implements EstadoEvaluacionQueryOutputPort {

    private final EstadoEvaluacionQueryRepository repository;

    @Override
    public List<EstadoEvaluacionReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(EstadoEvaluacionQueryMapper::toReadModel)
                .toList();
    }
}
