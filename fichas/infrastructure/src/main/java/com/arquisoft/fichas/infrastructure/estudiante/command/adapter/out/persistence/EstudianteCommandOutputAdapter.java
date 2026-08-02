package com.arquisoft.fichas.infrastructure.estudiante.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estudiante.port.out.EstudianteOutputPort;
import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteJpaRepository estudianteJpaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteJpaRepository.existsById(id);
    }
}
