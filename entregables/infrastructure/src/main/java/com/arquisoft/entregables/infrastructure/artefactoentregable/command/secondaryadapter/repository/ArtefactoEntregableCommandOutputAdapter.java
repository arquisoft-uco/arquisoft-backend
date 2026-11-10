package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.mapper.ArtefactoEntregableJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArtefactoEntregableCommandOutputAdapter implements ArtefactoEntregableOutputPort {

    private final ArtefactoCommandRepository artefactoCommandRepository;
    private final ArtefactoEntregableCommandRepository artefactoEntregableCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardarTodos(List<ArtefactoEntregableEntity> artefactos) {
        artefactoCommandRepository.saveAll(
                artefactos.stream().map(ArtefactoEntregableJpaMapper::toArtefactoJpaEntity).toList());
        artefactoEntregableCommandRepository.saveAll(
                artefactos.stream().map(ArtefactoEntregableJpaMapper::toJpaEntity).toList());
        logger.debug(ArtefactoEntregableKey.LOG_GUARDADOS,
                artefactos.getFirst().entregableProyectoGrado(), artefactos.size());
    }
}
