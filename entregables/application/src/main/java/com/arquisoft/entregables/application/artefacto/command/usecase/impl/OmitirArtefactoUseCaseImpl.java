package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.result.mapper.OmisionArtefactoResultMapper;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import com.arquisoft.entregables.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import com.arquisoft.shared.util.UtilFecha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirArtefactoUseCaseImpl implements OmitirArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AppLogger logger;

    @Override
    public OmisionArtefactoResult ejecutar(OmisionArtefactoDomain omision) {
        var artefacto = artefactoPorIdFinder.obtener(omision.getArtefacto());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_OMISION, !artefacto.esVacio(), artefacto.getOmisionOcurridaEn(),
                omision.getOcurridoEn());

        if (artefacto.esVacio()) {
            return OmisionArtefactoResultMapper.toResultSinVersionCerrada(omision);
        }
        if (artefacto.tieneOmisionIgualOPosteriorA(omision.getOcurridoEn())) {
            return OmisionArtefactoResultMapper.toResultDescartada(omision);
        }

        artefactoOutputPort.marcarOmision(omision.getArtefacto(), UtilFecha.generarInstanteActual(),
                omision.getOcurridoEn());
        return OmisionArtefactoResultMapper.toResultOmitida(omision);
    }
}
