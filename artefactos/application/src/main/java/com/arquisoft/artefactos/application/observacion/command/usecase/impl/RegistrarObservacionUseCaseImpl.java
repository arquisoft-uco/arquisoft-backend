package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionDescripcionExisteFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper.ObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.usecase.RegistrarObservacionUseCase;
import com.arquisoft.artefactos.application.observacion.command.validator.RegistrarObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RegistrarObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionUseCaseImpl implements RegistrarObservacionUseCase {

    private final RevisionAsesorFinder revisionAsesorFinder;
    private final ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    private final ObservacionDescripcionExisteFinder observacionDescripcionExisteFinder;
    private final RegistrarObservacionValidator registrarObservacionValidator;
    private final ObservacionOutputPort observacionOutputPort;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroObservacionDomain registro) {
        var observacion = registro.getObservacion();
        var revisionAsesor = observacion.getRevisionAsesor();

        logger.info(RegistrarObservacionKey.LOG_REGISTRANDO, revisionAsesor, registro.getAsesor());

        var revision = revisionAsesorFinder.obtener(revisionAsesor);
        var artefacto = artefactoPorRevisionAsesorFinder.obtener(revisionAsesor);
        var descripcionExiste = observacionDescripcionExisteFinder.obtener(observacion);

        logger.debug(RegistrarObservacionKey.LOG_VERIFICACION_REGISTRO,
                !revision.esVacio(), !artefacto.esVacio(), descripcionExiste);

        registrarObservacionValidator.validar(registro, revision, artefacto, descripcionExiste);

        observacionOutputPort.registrar(ObservacionMapper.toEntity(observacion));

        logger.info(RegistrarObservacionKey.LOG_REGISTRADA, observacion.getId(), revisionAsesor);
        return observacion.getId();
    }
}
