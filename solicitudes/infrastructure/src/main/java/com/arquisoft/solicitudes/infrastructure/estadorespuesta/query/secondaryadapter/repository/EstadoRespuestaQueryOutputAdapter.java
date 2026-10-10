package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.application.estadorespuesta.query.secondaryport.EstadoRespuestaQueryOutputPort;
import com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.secondaryadapter.repository.mapper.EstadoRespuestaQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoRespuestaQueryOutputAdapter implements EstadoRespuestaQueryOutputPort {

    private final EstadoRespuestaQueryRepository repository;

    @Override
    public List<EstadoRespuestaReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(EstadoRespuestaQueryMapper::toReadModel)
                .toList();
    }
}
