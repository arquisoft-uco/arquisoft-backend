package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.secondaryport.EstadoRevisionAsesorQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository.mapper.EstadoRevisionAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoRevisionAsesorQueryOutputAdapter implements EstadoRevisionAsesorQueryOutputPort {

    private final EstadoRevisionAsesorQueryRepository repository;

    @Override
    public List<EstadoRevisionAsesorReadModel> consultarTodos() {
        return repository.findAllByOrderByOrdenAsc()
                .stream()
                .map(EstadoRevisionAsesorQueryMapper::toReadModel)
                .toList();
    }
}
