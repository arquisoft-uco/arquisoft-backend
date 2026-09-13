package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichasCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteFichasCommandRepository estudianteRepository;
    private final AppLogger logger;

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

    @Override
    public void guardar(EstudianteEntity estudiante) {
        estudianteRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteKey.LOG_GUARDADO, estudiante.id());
    }

    @Override
    public Optional<EstudianteEntity> obtenerPorId(UUID id) {
        return estudianteRepository.findById(id).map(EstudianteJpaMapper::toEntity);
    }
}
