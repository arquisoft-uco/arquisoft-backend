package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoOmitidoDelDocumentoFinder;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.usecase.RegistrarArtefactoUseCase;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarArtefactoUseCaseImpl implements AsignarArtefactoUseCase {

    private final ArtefactoOmitidoDelDocumentoFinder artefactoOmitidoDelDocumentoFinder;
    private final RegistrarArtefactoUseCase registrarArtefactoUseCase;
    private final ReactivarArtefactoUseCase reactivarArtefactoUseCase;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AsignacionArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();
        logger.info(ArtefactoKey.LOG_ASIGNANDO, artefacto.getProyectoGrado(), artefacto.getAsesor());

        var omitido = artefactoOmitidoDelDocumentoFinder.obtener(artefacto);
        logger.debug(ArtefactoKey.LOG_VERIFICACION_ASIGNAR, !omitido.esVacio());

        var asignado = omitido.getId();
        if (omitido.esVacio()) {
            asignado = registrarArtefactoUseCase.ejecutar(registro);
        } else {
            reactivarArtefactoUseCase.ejecutar(ReactivacionArtefactoDomain.crear(
                    omitido.getId(), omitido.getProyectoGrado(), artefacto.getAsesor(),
                    registro.getEstudiantes().getEstudiantes()));
        }

        logger.info(ArtefactoKey.LOG_ASIGNADO, asignado, artefacto.getProyectoGrado());
        return asignado;
    }
}
