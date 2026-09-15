package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.mapper.CoordinadorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorCommandOutputAdapter implements CoordinadorOutputPort {

    private final CoordinadorCommandRepository coordinadorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(CoordinadorEntity coordinador) {
        coordinadorCommandRepository.save(CoordinadorJpaMapper.toJpaEntity(coordinador));
        logger.debug(CoordinadorKey.LOG_GUARDADO, coordinador.id());
    }

    @Override
    public Optional<CoordinadorEntity> obtenerPorId(UUID id) {
        return coordinadorCommandRepository.findById(id).map(CoordinadorJpaMapper::toEntity);
    }
}
