package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteArtefactoCommandOutputAdapter implements EstudianteArtefactoOutputPort {

    private final EstudianteArtefactoCommandRepository estudianteArtefactoCommandRepository;

    @Override
    public boolean existePorArtefactoYEstudiante(UUID artefacto, UUID estudiante) {
        return estudianteArtefactoCommandRepository.existsByArtefactoAndEstudiante(artefacto, estudiante);
    }
}
