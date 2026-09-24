package com.arquisoft.fichas.infrastructure.estadorevision.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.application.estadorevision.query.secondaryport.EstadoRevisionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadorevision.query.secondaryadapter.repository.mapper.EstadoRevisionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoRevisionQueryOutputAdapter implements EstadoRevisionQueryOutputPort {

    private final EstadoRevisionQueryRepository repository;

    @Override
    public List<EstadoRevisionReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(EstadoRevisionQueryMapper::toReadModel)
                .toList();
    }
}
