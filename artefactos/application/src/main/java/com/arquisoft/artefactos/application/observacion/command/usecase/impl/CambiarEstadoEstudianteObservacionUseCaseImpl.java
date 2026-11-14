package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.EstudianteParticipaObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.model.ParticipacionEstudianteObservacion;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoEstudianteObservacionUseCase;
import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoEstudianteObservacionValidator;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorObservacionFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoEstudianteObservacionKey;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEstudianteObservacionUseCaseImpl implements CambiarEstadoEstudianteObservacionUseCase {

    private final RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    private final ObservacionPorIdFinder observacionPorIdFinder;
    private final ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    private final EstudianteParticipaObservacionFinder estudianteParticipaObservacionFinder;
    private final CambiarEstadoEstudianteObservacionValidator cambiarEstadoEstudianteObservacionValidator;
    private final ObservacionOutputPort observacionOutputPort;
    private final ProyectoGradoBloqueadoPorObservacionFinder proyectoGradoBloqueadoPorObservacionFinder;
    private final ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoEstudianteObservacionDomain cambio) {
        var observacionId = cambio.getObservacion();
        var estado = cambio.getEstado();

        logger.info(CambiarEstadoEstudianteObservacionKey.LOG_CAMBIANDO_ESTADO,
                observacionId, cambio.getEstudiante(), estado.getId());

        var revision = revisionAsesorPorObservacionFinder.obtener(observacionId);
        var observacion = observacionPorIdFinder.obtener(observacionId);
        var artefacto = artefactoPorObservacionFinder.obtener(observacionId);
        var vinculado = estudianteParticipaObservacionFinder.obtener(
                new ParticipacionEstudianteObservacion(observacionId, cambio.getEstudiante()));

        var proyecto = proyectoGradoBloqueadoPorObservacionFinder.obtener(observacionId);
        logger.debug(CambiarEstadoEstudianteObservacionKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !observacion.esVacio(), !revision.esVacio(), !artefacto.esVacio(), vinculado);

        cambiarEstadoEstudianteObservacionValidator.validar(cambio, observacion, revision, artefacto, vinculado);

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAMIENTO, !proyecto.esVacio(), proyecto.estaCongelado());
        proyectoGradoNoCongeladoValidator.validar(proyecto);

        if (observacion.tieneEstado(estado)) {
            logger.info(CambiarEstadoEstudianteObservacionKey.LOG_ESTADO_SIN_CAMBIO, observacionId, estado.getId());
            return;
        }

        observacionOutputPort.actualizarEstado(observacionId, estado.getId());

        logger.info(CambiarEstadoEstudianteObservacionKey.LOG_ESTADO_CAMBIADO, observacionId, estado.getId());
    }
}
