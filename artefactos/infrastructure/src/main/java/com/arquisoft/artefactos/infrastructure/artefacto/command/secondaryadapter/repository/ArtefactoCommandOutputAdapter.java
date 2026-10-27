package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.mapper.ArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoCommandOutputAdapter implements ArtefactoOutputPort {

    private final ArtefactoCommandRepository artefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrarTodos(List<ArtefactoEntity> artefactos) {
        artefactoCommandRepository.saveAll(artefactos.stream().map(ArtefactoJpaMapper::toJpaEntity).toList());
        logger.debug(ArtefactoKey.LOG_GUARDADOS, artefactos.size());
    }

    @Override
    public void traspasarAsesorDeProyecto(UUID proyectoGrado, UUID asesor) {
        var traspasados = artefactoCommandRepository.traspasarAsesorDeProyecto(proyectoGrado, asesor);
        logger.debug(ArtefactoKey.LOG_TRASPASADOS, traspasados);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorId(UUID id) {
        return artefactoCommandRepository.findById(id).map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerBloqueadoPorId(UUID id) {
        return artefactoCommandRepository.bloquearPorId(id).map(ArtefactoJpaMapper::toEntity);
    }
}
