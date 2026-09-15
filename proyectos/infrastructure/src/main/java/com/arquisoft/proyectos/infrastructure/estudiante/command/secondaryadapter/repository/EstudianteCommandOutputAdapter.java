package com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(EstudianteEntity estudiante) {
        estudianteCommandRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteProyectosKey.LOG_GUARDADO, estudiante.id());
    }

    @Override
    public Optional<EstudianteEntity> obtenerPorId(UUID id) {
        return estudianteCommandRepository.findById(id).map(EstudianteJpaMapper::toEntity);
    }
}
