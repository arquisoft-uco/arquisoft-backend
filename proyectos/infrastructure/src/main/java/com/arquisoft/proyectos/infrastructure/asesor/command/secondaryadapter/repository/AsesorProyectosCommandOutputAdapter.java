package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.mapper.AsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectosCommandOutputAdapter implements AsesorOutputPort {

    private final AsesorProyectosCommandRepository asesorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(AsesorEntity asesor) {
        asesorCommandRepository.save(AsesorJpaMapper.toJpaEntity(asesor));
        logger.debug(AsesorKey.LOG_GUARDADO, asesor.id());
    }

    @Override
    public Optional<AsesorEntity> obtenerPorId(UUID id) {
        return asesorCommandRepository.findById(id).map(AsesorJpaMapper::toEntity);
    }
}
