package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.application.estadoitem.query.secondaryport.EstadoItemQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository.mapper.EstadoItemQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoItemQueryOutputAdapter implements EstadoItemQueryOutputPort {

    private final EstadoItemQueryRepository repository;

    @Override
    public List<EstadoItemReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(EstadoItemQueryMapper::toReadModel)
                .toList();
    }
}
