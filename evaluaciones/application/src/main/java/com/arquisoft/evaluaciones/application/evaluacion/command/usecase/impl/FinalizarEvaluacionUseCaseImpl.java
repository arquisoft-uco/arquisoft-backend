package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.ContactoAsesorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.ContactoCoordinadorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.AvanceEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ObservacionesEvaluacionExistenFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.PesosFinalizacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.FinalizacionEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.ConsolidarNotasEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.FinalizarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.FinalizarEvaluacionValidator;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.ContactosEstudiantesEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ResumenEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ResumenesEvaluacionJuradosFinder;
import com.arquisoft.evaluaciones.domain.evaluacion.ConsolidacionNotasEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.FinalizacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.VerificacionFinalizacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.event.EvaluacionFinalizadaEvent;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarEvaluacionUseCaseImpl implements FinalizarEvaluacionUseCase {

    private final AvanceEvaluacionFinder avanceEvaluacionFinder;
    private final ResumenEvaluacionAsesorFinder resumenEvaluacionAsesorFinder;
    private final ResumenesEvaluacionJuradosFinder resumenesEvaluacionJuradosFinder;
    private final PesosFinalizacionFinder pesosFinalizacionFinder;
    private final ContactoAsesorEvaluacionFinder contactoAsesorEvaluacionFinder;
    private final ContactoCoordinadorEvaluacionFinder contactoCoordinadorEvaluacionFinder;
    private final ContactosEstudiantesEvaluacionFinder contactosEstudiantesEvaluacionFinder;
    private final ObservacionesEvaluacionExistenFinder observacionesEvaluacionExistenFinder;
    private final FinalizarEvaluacionValidator validator;
    private final ConsolidarNotasEvaluacionUseCase consolidarNotasEvaluacionUseCase;
    private final EvaluacionOutputPort evaluacionOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(VerificacionFinalizacionEvaluacionDomain verificacion) {
        var evaluacion = verificacion.getEvaluacion();

        var avance = avanceEvaluacionFinder.obtener(evaluacion);
        logger.debug(EvaluacionKey.LOG_VERIFICACION_FINALIZACION, evaluacion, avance.getEstado().getId(),
                avance.getProgreso().juradosVigentes(), avance.getProgreso().juradosEvaluados());

        if (!avance.puedeFinalizarse()) {
            return;
        }

        var resumenAsesor = resumenEvaluacionAsesorFinder.obtener(evaluacion);
        var resumenesJurados = resumenesEvaluacionJuradosFinder.obtener(evaluacion);
        var pesos = pesosFinalizacionFinder.obtener();
        var asesor = contactoAsesorEvaluacionFinder.obtener(evaluacion);
        var coordinador = contactoCoordinadorEvaluacionFinder.obtener(evaluacion);
        var estudiantes = contactosEstudiantesEvaluacionFinder.obtener(evaluacion);
        var hayObservaciones = observacionesEvaluacionExistenFinder.obtener(evaluacion);

        var destinatarios = new DestinatariosEvaluacionFinalizada(evaluacion, coordinador, estudiantes, asesor);
        validator.validar(
                new ResumenesEvaluacion(
                        evaluacion, resumenAsesor, resumenesJurados, avance.getProgreso().juradosEvaluados()),
                destinatarios);

        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                evaluacion, resumenAsesor, resumenesJurados, pesos);
        consolidarNotasEvaluacionUseCase.ejecutar(consolidacion);

        var finalizacion = FinalizacionEvaluacionDomain.crear(consolidacion, hayObservaciones);
        evaluacionOutputPort.finalizar(FinalizacionEvaluacionMapper.toEntity(finalizacion));

        eventPublisher.publish(new EvaluacionFinalizadaEvent(
                evaluacion, avance.getEntregable(), finalizacion.getResultado(),
                finalizacion.getNotaFinal(), destinatarios));

        logger.debug(EvaluacionKey.LOG_FINALIZADA, evaluacion,
                finalizacion.getResultado().getId(), finalizacion.getNotaFinal());
    }
}
