package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.usecase.TraspasarArtefactosUseCase;
import com.arquisoft.artefactos.domain.artefacto.TraspasoArtefactosDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraspasarArtefactosUseCaseImpl implements TraspasarArtefactosUseCase {

    private final ArtefactoOutputPort artefactoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(TraspasoArtefactosDomain traspaso) {
        logger.debug(ArtefactoKey.LOG_TRASPASANDO, traspaso.getProyectoGrado(), traspaso.getAsesor());
        artefactoOutputPort.traspasarAsesorDeProyecto(traspaso.getProyectoGrado(), traspaso.getAsesor());
    }
}
