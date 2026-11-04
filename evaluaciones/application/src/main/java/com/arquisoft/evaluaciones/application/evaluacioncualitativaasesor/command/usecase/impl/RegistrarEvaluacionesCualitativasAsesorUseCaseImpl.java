package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.CriteriosCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.ItemsEvaluacionCualitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.model.ItemsPorEvaluacionAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.mapper.EvaluacionCualitativaAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.RegistrarEvaluacionesCualitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.RegistrarEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemsCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.RegistroEvaluacionesCualitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.event.EvaluacionesCualitativasAsesorRegistradasEvent;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaItemsCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCualitativasAsesorUseCaseImpl
        implements RegistrarEvaluacionesCualitativasAsesorUseCase {

    private final ContextoRegistroEvaluacionAsesorFinder contextoRegistroEvaluacionAsesorFinder;
    private final ItemsCualitativosAsesorExistentesFinder itemsCualitativosAsesorExistentesFinder;
    private final CriteriosCualitativosAsesorExistentesFinder criteriosCualitativosAsesorExistentesFinder;
    private final ItemsEvaluacionCualitativaAsesorRegistradosFinder itemsEvaluacionCualitativaAsesorRegistradosFinder;
    private final RegistrarEvaluacionesCualitativasAsesorValidator validator;
    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;
    private final IniciarEvaluacionUseCase iniciarEvaluacionUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistroEvaluacionesCualitativasAsesorDomain registro) {
        var evaluacionAsesor = registro.getEvaluacionAsesor();

        logger.info(EvaluacionCualitativaAsesorKey.LOG_REGISTRANDO_LOTE,
                evaluacionAsesor, registro.getEvaluaciones().size());

        var itemsSolicitados = registro.getEvaluaciones().stream()
                .map(EvaluacionCualitativaAsesorDomain::getItem)
                .collect(Collectors.toUnmodifiableSet());
        var criteriosSolicitados = registro.getEvaluaciones().stream()
                .map(EvaluacionCualitativaAsesorDomain::getCriterio)
                .collect(Collectors.toUnmodifiableSet());

        var contexto = contextoRegistroEvaluacionAsesorFinder.obtener(evaluacionAsesor);
        var itemsExistentes = itemsCualitativosAsesorExistentesFinder.obtener(itemsSolicitados);
        var criteriosExistentes = criteriosCualitativosAsesorExistentesFinder.obtener(criteriosSolicitados);
        var itemsRegistrados = itemsEvaluacionCualitativaAsesorRegistradosFinder.obtener(
                new ItemsPorEvaluacionAsesor(evaluacionAsesor, itemsSolicitados));

        logger.debug(EvaluacionCualitativaAsesorKey.LOG_VERIFICACION_LOTE,
                evaluacionAsesor, !contexto.esVacio(), itemsExistentes.size(),
                criteriosExistentes.size(), itemsRegistrados.size());

        validator.validar(
                new ExistenciaEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor()),
                new PropiedadEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor(), registro.getAsesor()),
                new ExistenciaItemsCualitativosAsesor(itemsSolicitados, itemsExistentes),
                new ExistenciaCriteriosCualitativosAsesor(criteriosSolicitados, criteriosExistentes),
                new DisponibilidadEvaluacionesCualitativasAsesor(
                        evaluacionAsesor, itemsSolicitados, itemsRegistrados));

        iniciarEvaluacionUseCase.ejecutar(InicioEvaluacionDomain.crear(
                contexto.getEvaluacion(), contexto.getEstado()));

        var entidades = registro.getEvaluaciones().stream()
                .map(EvaluacionCualitativaAsesorMapper::toEntity)
                .toList();
        evaluacionCualitativaAsesorOutputPort.registrarTodas(entidades);

        var evento = new EvaluacionesCualitativasAsesorRegistradasEvent(contexto.getEntregable());
        eventPublisher.publish(evento);

        logger.info(EvaluacionCualitativaAsesorKey.LOG_LOTE_REGISTRADO,
                evaluacionAsesor, entidades.size(), evento.getIdEvento());
    }
}
