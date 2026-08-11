package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estudiante.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteRepository estudianteRepository;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteRepository.existsById(id);
    }
}
