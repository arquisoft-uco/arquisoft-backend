package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteRepository;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteRepository.existsById(id);
    }
}
