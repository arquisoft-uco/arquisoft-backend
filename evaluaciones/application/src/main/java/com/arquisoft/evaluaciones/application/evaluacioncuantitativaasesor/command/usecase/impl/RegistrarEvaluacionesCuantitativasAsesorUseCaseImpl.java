package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemsEvaluacionCuantitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper.EvaluacionCuantitativaAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.RegistrarEvaluacionesCuantitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.RegistrarEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ValoresMaximosItemsCuantitativosAsesorFinder;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.EvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.RegistroEvaluacionesCuantitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.event.EvaluacionesCuantitativasAsesorRegistradasEvent;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCuantitativasAsesorUseCaseImpl
        implements RegistrarEvaluacionesCuantitativasAsesorUseCase {

    private final ContextoRegistroEvaluacionAsesorFinder contextoRegistroEvaluacionAsesorFinder;
    private final ValoresMaximosItemsCuantitativosAsesorFinder valoresMaximosItemsCuantitativosAsesorFinder;
    private final ItemsEvaluacionCuantitativaAsesorRegistradosFinder itemsEvaluacionCuantitativaAsesorRegistradosFinder;
    private final RegistrarEvaluacionesCuantitativasAsesorValidator validator;
    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;
    private final IniciarEvaluacionUseCase iniciarEvaluacionUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistroEvaluacionesCuantitativasAsesorDomain registro) {
        var evaluacionAsesor = registro.getEvaluacionAsesor();

        logger.info(EvaluacionCuantitativaAsesorKey.LOG_REGISTRANDO_LOTE,
                evaluacionAsesor, registro.getEvaluaciones().size());

        var itemsSolicitados = registro.getEvaluaciones().stream()
                .map(EvaluacionCuantitativaAsesorDomain::getItem)
                .collect(Collectors.toUnmodifiableSet());
        var puntajes = registro.getEvaluaciones().stream()
                .collect(Collectors.toUnmodifiableMap(
                        EvaluacionCuantitativaAsesorDomain::getItem,
                        EvaluacionCuantitativaAsesorDomain::getPuntaje));

        var contexto = contextoRegistroEvaluacionAsesorFinder.obtener(evaluacionAsesor);
        var valoresMaximos = valoresMaximosItemsCuantitativosAsesorFinder.obtener(itemsSolicitados);
        var itemsRegistrados = itemsEvaluacionCuantitativaAsesorRegistradosFinder.obtener(
                new ItemsPorEvaluacionCuantitativaAsesor(evaluacionAsesor, itemsSolicitados));

        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_VERIFICACION_LOTE,
                evaluacionAsesor, !contexto.esVacio(), valoresMaximos.size(), itemsRegistrados.size());

        validator.validar(
                new ExistenciaEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor()),
                new PropiedadEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor(), registro.getAsesor()),
                new ExistenciaItemsCuantitativosAsesor(itemsSolicitados, valoresMaximos),
                new PuntajesVsValoresMaximosAsesor(puntajes, valoresMaximos),
                new DisponibilidadEvaluacionesCuantitativasAsesor(
                        evaluacionAsesor, itemsSolicitados, itemsRegistrados));

        iniciarEvaluacionUseCase.ejecutar(InicioEvaluacionDomain.crear(
                contexto.getEvaluacion(), contexto.getEstado()));

        var entidades = registro.getEvaluaciones().stream()
                .map(EvaluacionCuantitativaAsesorMapper::toEntity)
                .toList();
        evaluacionCuantitativaAsesorOutputPort.registrarTodas(entidades);

        var evento = new EvaluacionesCuantitativasAsesorRegistradasEvent(contexto.getEntregable());
        eventPublisher.publish(evento);

        logger.info(EvaluacionCuantitativaAsesorKey.LOG_LOTE_REGISTRADO,
                evaluacionAsesor, entidades.size(), evento.getIdEvento());
    }
}
