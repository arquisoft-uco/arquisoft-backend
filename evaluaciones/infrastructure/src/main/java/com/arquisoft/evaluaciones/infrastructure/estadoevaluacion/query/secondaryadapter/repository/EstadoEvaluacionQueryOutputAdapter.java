package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository.mapper.EstadoEvaluacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionQueryOutputAdapter implements EstadoEvaluacionQueryOutputPort {

    private final EstadoEvaluacionQueryRepository repository;

    @Override
    public List<EstadoEvaluacionReadModel> consultarTodos() {
        return repository.findAllByOrderByOrdenAsc()
                .stream()
                .map(EstadoEvaluacionQueryMapper::toReadModel)
                .toList();
    }
}
