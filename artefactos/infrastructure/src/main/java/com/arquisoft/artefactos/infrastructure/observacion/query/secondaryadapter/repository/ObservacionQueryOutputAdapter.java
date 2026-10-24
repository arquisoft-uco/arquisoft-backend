package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.secondaryport.ObservacionQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.mapper.ObservacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionQueryOutputAdapter implements ObservacionQueryOutputPort {

    private final ObservacionQueryRepository observacionQueryRepository;

    @Override
    public List<ObservacionReadModel> consultarPorRevisionAsesor(UUID revisionAsesor) {
        return observacionQueryRepository.findByRevisionAsesorOrderByDescripcionAscIdAsc(revisionAsesor).stream()
                .map(ObservacionQueryMapper::toReadModel)
                .toList();
    }
}
