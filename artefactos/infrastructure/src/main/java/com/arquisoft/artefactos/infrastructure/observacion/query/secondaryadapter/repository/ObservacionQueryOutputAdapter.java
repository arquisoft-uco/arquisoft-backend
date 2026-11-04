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
    private final ObservacionEstudianteQueryRepository observacionEstudianteQueryRepository;

    @Override
    public List<ObservacionReadModel> consultarDeAsesor(UUID revisionAsesor, UUID asesor) {
        return observacionQueryRepository
                .findByRevisionAsesorAndAsesorOrderByDescripcionAscIdAsc(revisionAsesor, asesor).stream()
                .map(ObservacionQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ObservacionReadModel> consultarDeEstudiante(UUID revisionAsesor, UUID estudiante) {
        return observacionEstudianteQueryRepository
                .findByRevisionAsesorAndEstudianteOrderByDescripcionAscIdAsc(revisionAsesor, estudiante).stream()
                .map(ObservacionQueryMapper::toReadModel)
                .toList();
    }
}
