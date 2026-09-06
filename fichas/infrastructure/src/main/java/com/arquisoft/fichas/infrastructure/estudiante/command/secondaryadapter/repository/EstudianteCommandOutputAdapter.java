package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteRepository;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteRepository.existsById(id);
    }

    @Override
    public List<EstudianteEntity> buscarPorIds(List<UUID> ids) {
        return estudianteRepository.findAllById(ids).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }
}
