package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarAsesorArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarAsesorArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarAsesorArtefactoUseCaseImpl implements CambiarAsesorArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    private final CambiarAsesorArtefactoValidator cambiarAsesorArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioAsesorArtefactoDomain cambio) {
        logger.info(ArtefactoKey.LOG_CAMBIANDO_ASESOR, cambio.getArtefacto(), cambio.getAsesor());

        var artefacto = artefactoPorIdFinder.obtener(cambio.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(cambio.getAsesor());
        var vinculosMasRecientes = vinculosMasRecientesProyectoGradoFinder.obtener(artefacto.getProyectoGrado());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_CAMBIAR_ASESOR,
                !artefacto.esVacio(), !asesor.esVacio() && !asesor.estaEliminado(), vinculosMasRecientes.size());

        cambiarAsesorArtefactoValidator.validar(cambio, artefacto, asesor, vinculosMasRecientes);

        artefactoOutputPort.cambiarAsesor(cambio.getArtefacto(), cambio.getAsesor());

        logger.info(ArtefactoKey.LOG_ASESOR_CAMBIADO, cambio.getArtefacto(), cambio.getAsesor());
    }
}
