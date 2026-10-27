package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteCommandRepository;

    @Override
    public List<EstudianteEntity> obtenerPorIds(List<UUID> ids) {
        return estudianteCommandRepository.findAllById(ids).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }
}
