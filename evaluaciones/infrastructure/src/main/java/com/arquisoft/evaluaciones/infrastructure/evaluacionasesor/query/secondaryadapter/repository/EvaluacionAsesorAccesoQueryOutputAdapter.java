package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorAccesoQueryOutputAdapter implements EvaluacionAsesorAccesoQueryOutputPort {

    private final EvaluacionAsesorAccesoQueryRepository repository;
    private final EstudianteEvaluacionAsesorAccesoQueryRepository estudianteRepository;

    @Override
    public Optional<UUID> obtenerAsesorPorId(UUID evaluacionAsesor) {
        return repository.findById(evaluacionAsesor).map(EvaluacionAsesorAccesoJpaQueryEntity::getAsesorId);
    }

    @Override
    public Optional<UUID> obtenerPorEvaluacion(UUID evaluacion) {
        return repository.findByEvaluacionId(evaluacion).map(EvaluacionAsesorAccesoJpaQueryEntity::getId);
    }

    @Override
    public List<UUID> obtenerEstudiantesVigentesPorId(UUID evaluacionAsesor) {
        return estudianteRepository.findByEvaluacionAsesorId(evaluacionAsesor).stream()
                .map(EstudianteEvaluacionAsesorAccesoJpaQueryEntity::getEstudianteId)
                .toList();
    }
}
