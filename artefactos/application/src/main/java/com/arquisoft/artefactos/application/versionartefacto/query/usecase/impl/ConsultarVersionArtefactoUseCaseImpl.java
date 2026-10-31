package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.AsesorDeVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.EstudianteParticipaVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.ConsultarVersionArtefactoValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarVersionArtefactoUseCaseImpl implements ConsultarVersionArtefactoUseCase {

    private final AsesorDeVersionArtefactoQueryFinder asesorDeVersionArtefactoQueryFinder;
    private final EstudianteParticipaVersionArtefactoQueryFinder estudianteParticipaVersionArtefactoQueryFinder;
    private final ConsultarVersionArtefactoValidator consultarVersionArtefactoValidator;
    private final VersionArtefactoQueryOutputPort versionArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public VersionArtefactoReadModel ejecutar(VersionArtefactoDetalleCriteria entrada) {
        logger.debug(VersionArtefactoKey.LOG_CONSULTANDO_DETALLE, entrada.versionArtefacto(), entrada.actor());

        var asesor = asesorDeVersionArtefactoQueryFinder.obtener(entrada.versionArtefacto());
        var participa = estudianteParticipaVersionArtefactoQueryFinder.obtener(
                new ParticipacionVersionArtefacto(entrada.versionArtefacto(), entrada.actor()));

        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_CONSULTA_DETALLE,
                !UtilUUID.esPorDefecto(asesor), participa);

        consultarVersionArtefactoValidator.validar(entrada, asesor, participa);

        var version = versionArtefactoQueryOutputPort.consultarPorId(entrada.versionArtefacto());

        logger.debug(VersionArtefactoKey.LOG_CONSULTA_DETALLE_COMPLETADA, entrada.versionArtefacto());
        return version;
    }
}
