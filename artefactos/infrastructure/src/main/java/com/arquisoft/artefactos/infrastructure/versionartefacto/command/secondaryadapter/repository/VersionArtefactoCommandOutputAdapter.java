package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.mapper.VersionArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoCommandOutputAdapter implements VersionArtefactoOutputPort {

    private final VersionArtefactoCommandRepository versionArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(VersionArtefactoEntity version) {
        versionArtefactoCommandRepository.save(VersionArtefactoJpaMapper.toJpaEntity(version));
        logger.debug(VersionArtefactoKey.LOG_GUARDADA, version.id());
    }

    @Override
    public int ultimoNumeroPorArtefacto(UUID artefacto) {
        return versionArtefactoCommandRepository.obtenerUltimoNumero(artefacto);
    }
}
