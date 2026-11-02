package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper.VersionRepositorioArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoCommandOutputAdapter implements VersionRepositorioArtefactoOutputPort {

    private final VersionRepositorioArtefactoCommandRepository versionRepositorioArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(VersionRepositorioArtefactoEntity version) {
        versionRepositorioArtefactoCommandRepository.save(VersionRepositorioArtefactoJpaMapper.toJpaEntity(version));
        logger.debug(VersionRepositorioArtefactoKey.LOG_GUARDADA, version.id());
    }

    @Override
    public Optional<VersionRepositorioArtefactoEntity> obtenerUltimaVersionDocumento(
            UUID repositorio, String nombreDocumento) {
        return versionRepositorioArtefactoCommandRepository
                .findFirstByRepositorioArtefactoAndNombreDocumentoIgnoreCaseOrderByNumeroVersionDesc(
                        repositorio, nombreDocumento)
                .map(VersionRepositorioArtefactoJpaMapper::toEntity);
    }
}
