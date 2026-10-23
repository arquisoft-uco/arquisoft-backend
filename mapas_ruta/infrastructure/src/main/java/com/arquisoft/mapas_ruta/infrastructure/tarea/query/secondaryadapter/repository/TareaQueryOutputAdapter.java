package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.mapper.TareaQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TareaQueryOutputAdapter implements TareaQueryOutputPort {

    private final TareaQueryRepository tareaQueryRepository;

    @Override
    public Optional<TareaReadModel> consultarPorId(UUID tarea) {
        return tareaQueryRepository.findById(tarea)
                .map(TareaQueryMapper::toReadModel);
    }
}
