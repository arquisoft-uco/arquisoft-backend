package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.OmitirArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorArtefactoFinder;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoOmitidoEvent;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirArtefactoUseCaseImpl implements OmitirArtefactoUseCase {

    private final ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionRepositorioArtefactoPorArtefactoFinder versionRepositorioArtefactoPorArtefactoFinder;
    private final EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    private final OmitirArtefactoValidator omitirArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(OmisionArtefactoDomain omision) {
        logger.info(ArtefactoKey.LOG_OMITIENDO, omision.getArtefacto(), omision.getAsesor());

        var artefacto = artefactoBloqueadoPorIdFinder.obtener(omision.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(omision.getAsesor());
        var versionRepositorio = versionRepositorioArtefactoPorArtefactoFinder.obtener(omision.getArtefacto());
        var estudiantes = estudiantesVinculadosArtefactoFinder.obtener(omision.getArtefacto());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_OMITIR,
                !artefacto.esVacio(), !asesor.esVacio(), !versionRepositorio.esVacio(), estudiantes.size());

        omitirArtefactoValidator.validar(omision, artefacto, asesor, versionRepositorio);

        artefacto.omitir(UtilFecha.generarInstanteActual());
        artefactoOutputPort.omitir(artefacto.getId(), artefacto.getOmitidoEn());

        eventPublisher.publish(new ArtefactoOmitidoEvent(
                versionRepositorio.getNombreDocumento(),
                estudiantes.stream()
                        .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                        .toList(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(ArtefactoKey.LOG_OMITIDO, omision.getArtefacto(), omision.getAsesor());
    }
}
