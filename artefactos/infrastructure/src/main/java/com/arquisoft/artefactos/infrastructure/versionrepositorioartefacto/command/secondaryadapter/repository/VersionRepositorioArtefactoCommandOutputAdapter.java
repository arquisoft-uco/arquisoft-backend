package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper.VersionRepositorioArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoCommandOutputAdapter implements VersionRepositorioArtefactoOutputPort {

    private final VersionRepositorioArtefactoCommandRepository versionRepositorioArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(VersionRepositorioArtefactoEntity version) {
        versionRepositorioArtefactoCommandRepository.save(VersionRepositorioArtefactoJpaMapper.toJpaEntity(version));
        logger.debug(AgregarVersionRepositorioArtefactoKey.LOG_GUARDADA, version.id());
    }

    @Override
    public boolean existePorId(UUID id) {
        return versionRepositorioArtefactoCommandRepository.existsById(id);
    }
}
