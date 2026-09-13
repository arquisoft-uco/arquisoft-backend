package com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.mapper.CoordinadorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorFichasCommandOutputAdapter implements CoordinadorOutputPort {

    private final CoordinadorFichasCommandRepository coordinadorCommandRepository;
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
