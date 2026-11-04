package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.mapper.EstadoProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoProyectoGradoCommandOutputAdapter implements EstadoProyectoGradoOutputPort {

    private final EstadoProyectoGradoCommandRepository estadoProyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void registrar(EstadoProyectoGradoEntity estado) {
        estadoProyectoGradoRepository.save(EstadoProyectoGradoJpaMapper.toJpaEntity(estado));
        logger.debug(EstadoProyectoGradoKey.LOG_GUARDADO, estado.id(), estado.proyectoGrado());
    }

    @Override
    public Optional<EstadoProyectoGradoEntity> obtenerEstadoActual(UUID proyectoGrado) {
        return estadoProyectoGradoRepository
                .findFirstByProyectoGradoIdOrderByFechaActualizacionDesc(proyectoGrado)
                .map(EstadoProyectoGradoJpaMapper::toEntity);
    }
}
