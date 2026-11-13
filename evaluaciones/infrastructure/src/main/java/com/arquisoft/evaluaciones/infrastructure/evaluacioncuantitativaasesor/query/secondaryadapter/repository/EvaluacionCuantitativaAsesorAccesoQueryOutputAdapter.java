package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorAccesoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.mapper.EvaluacionCuantitativaAsesorAccesoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaAsesorAccesoQueryOutputAdapter
        implements EvaluacionCuantitativaAsesorAccesoQueryOutputPort {

    private final EvaluacionCuantitativaAsesorAccesoQueryRepository repository;

    @Override
    public Optional<EvaluacionCuantitativaAsesorAccesoEntity> obtenerAccesoPorId(UUID evaluacionCuantitativaAsesor) {
        return repository.findById(evaluacionCuantitativaAsesor)
                .map(EvaluacionCuantitativaAsesorAccesoQueryMapper::toEntity);
    }
}
