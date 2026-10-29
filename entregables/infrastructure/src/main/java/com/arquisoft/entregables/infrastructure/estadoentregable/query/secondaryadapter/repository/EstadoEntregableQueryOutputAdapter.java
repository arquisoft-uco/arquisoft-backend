package com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.application.estadoentregable.query.secondaryport.EstadoEntregableQueryOutputPort;
import com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository.mapper.EstadoEntregableQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoEntregableQueryOutputAdapter implements EstadoEntregableQueryOutputPort {

    private final EstadoEntregableQueryRepository repository;

    @Override
    public List<EstadoEntregableReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(EstadoEntregableQueryMapper::toReadModel)
                .toList();
    }
}
