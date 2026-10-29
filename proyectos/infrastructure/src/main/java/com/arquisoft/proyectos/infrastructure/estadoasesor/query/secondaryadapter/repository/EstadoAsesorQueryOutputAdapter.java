package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository.mapper.EstadoAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoAsesorQueryOutputAdapter implements EstadoAsesorQueryOutputPort {

    private final EstadoAsesorQueryRepository repository;

    @Override
    public List<EstadoAsesorReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(EstadoAsesorQueryMapper::toReadModel)
                .toList();
    }
}
