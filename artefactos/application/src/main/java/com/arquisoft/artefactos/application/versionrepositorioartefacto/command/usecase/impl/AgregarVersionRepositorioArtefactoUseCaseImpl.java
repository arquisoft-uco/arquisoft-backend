package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.mapper.AgregacionVersionRepositorioArtefactoResultMapper;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.AgregarVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarVersionRepositorioArtefactoUseCaseImpl implements AgregarVersionRepositorioArtefactoUseCase {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    private final VersionRepositorioArtefactoExisteFinder versionRepositorioArtefactoExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionVersionRepositorioArtefactoResult ejecutar(VersionRepositorioArtefactoDomain version) {
        var existe = versionRepositorioArtefactoExisteFinder.obtener(version.getId());
        logger.debug(AgregarVersionRepositorioArtefactoKey.LOG_VERIFICACION_AGREGAR, version.getId(), existe);

        if (existe) {
            return AgregacionVersionRepositorioArtefactoResultMapper.toResultDuplicada(version);
        }

        versionRepositorioArtefactoOutputPort.guardar(VersionRepositorioArtefactoMapper.toEntity(version));
        return AgregacionVersionRepositorioArtefactoResultMapper.toResultAgregada(version);
    }
}
