package com.arquisoft.fichas.infrastructure.estudiante.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteQueryOutputAdapter implements EstudianteQueryOutputPort {

    private final EstudianteJpaRepository jpaRepository;

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
