package com.arquisoft.fichas.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.query.secondaryport.EstudianteQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteQueryOutputAdapter implements EstudianteQueryOutputPort {

    private final EstudianteQueryRepository estudianteRepository;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteRepository.existsById(id);
    }
}
