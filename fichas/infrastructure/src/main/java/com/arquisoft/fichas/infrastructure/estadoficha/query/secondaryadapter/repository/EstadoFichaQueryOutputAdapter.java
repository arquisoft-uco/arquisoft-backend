package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository.mapper.EstadoFichaQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoFichaQueryOutputAdapter implements EstadoFichaQueryOutputPort {

    private final EstadoFichaQueryRepository repository;

    @Override
    public List<EstadoFichaReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(EstadoFichaQueryMapper::toReadModel)
                .toList();
    }
}
