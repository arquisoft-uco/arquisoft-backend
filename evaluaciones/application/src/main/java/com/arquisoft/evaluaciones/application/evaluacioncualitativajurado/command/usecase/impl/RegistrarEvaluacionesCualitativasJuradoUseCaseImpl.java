package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.CriteriosCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.ItemsEvaluacionCualitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model.CriterioItemsEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.mapper.EvaluacionCualitativaJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.RegistrarEvaluacionesCualitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.RegistrarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemsCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.RegistroEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event.EvaluacionesCualitativasJuradoRegistradasEvent;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCualitativasJuradoUseCaseImpl
        implements RegistrarEvaluacionesCualitativasJuradoUseCase {

    private final ContextoRegistroEvaluacionJuradoFinder contextoRegistroEvaluacionJuradoFinder;
    private final ItemsCualitativosJuradoExistentesFinder itemsCualitativosJuradoExistentesFinder;
    private final CriteriosCualitativosJuradoExistentesFinder criteriosCualitativosJuradoExistentesFinder;
    private final ItemsEvaluacionCualitativaJuradoRegistradosFinder itemsEvaluacionCualitativaJuradoRegistradosFinder;
    private final RegistrarEvaluacionesCualitativasJuradoValidator validator;
    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;
    private final IniciarEvaluacionUseCase iniciarEvaluacionUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistroEvaluacionesCualitativasJuradoDomain registro) {
        UUID evaluacionJurado = registro.getEvaluaciones().getFirst().getEvaluacionJurado();

        logger.info(EvaluacionCualitativaJuradoKey.LOG_REGISTRANDO_LOTE,
                evaluacionJurado, registro.getEvaluaciones().size());

        ContextoRegistroEvaluacionJuradoEntity contexto = validarExistenciaYObtenerContexto(evaluacionJurado);

        validarContenidoDelLote(registro, evaluacionJurado);

        iniciarEvaluacionUseCase.ejecutar(InicioEvaluacionDomain.crear(
                contexto.evaluacion(), EstadoEvaluacion.desde(contexto.estado())));

        var entidades = registro.getEvaluaciones().stream()
                .map(EvaluacionCualitativaJuradoMapper::toEntity)
                .toList();
        evaluacionCualitativaJuradoOutputPort.registrarTodas(entidades);

        var evento = publicarEvento(contexto);

        logger.info(EvaluacionCualitativaJuradoKey.LOG_LOTE_REGISTRADO,
                evaluacionJurado, entidades.size(), evento.getIdEvento());
    }

    private ContextoRegistroEvaluacionJuradoEntity validarExistenciaYObtenerContexto(UUID evaluacionJurado) {
        var contextoOpt = contextoRegistroEvaluacionJuradoFinder.obtener(evaluacionJurado);

        validator.validarExistencia(new ExistenciaEvaluacionJurado(evaluacionJurado, contextoOpt.isPresent()));

        return contextoOpt.orElseThrow();
    }

    private void validarContenidoDelLote(RegistroEvaluacionesCualitativasJuradoDomain registro, UUID evaluacionJurado) {
        Set<UUID> itemsSolicitados = registro.getEvaluaciones().stream()
                .map(evaluacion -> evaluacion.getItem())
                .collect(Collectors.toUnmodifiableSet());
        Set<UUID> criteriosSolicitados = registro.getEvaluaciones().stream()
                .map(evaluacion -> evaluacion.getCriterio())
                .collect(Collectors.toUnmodifiableSet());

        Set<UUID> itemsExistentes = itemsCualitativosJuradoExistentesFinder.obtener(itemsSolicitados);
        Set<UUID> criteriosExistentes = criteriosCualitativosJuradoExistentesFinder.obtener(criteriosSolicitados);
        Set<UUID> itemsRegistrados = itemsEvaluacionCualitativaJuradoRegistradosFinder.obtener(
                new CriterioItemsEvaluacion(evaluacionJurado, itemsSolicitados));

        logger.debug(EvaluacionCualitativaJuradoKey.LOG_VERIFICACION_LOTE,
                evaluacionJurado, itemsExistentes.size(), criteriosExistentes.size());

        validator.validarContenido(
                new ExistenciaItemsCualitativosJurado(itemsSolicitados, itemsExistentes),
                new ExistenciaCriteriosCualitativosJurado(criteriosSolicitados, criteriosExistentes),
                new DisponibilidadEvaluacionesCualitativasJurado(evaluacionJurado, itemsSolicitados, itemsRegistrados));
    }

    private EvaluacionesCualitativasJuradoRegistradasEvent publicarEvento(
            ContextoRegistroEvaluacionJuradoEntity contexto) {
        var evento = new EvaluacionesCualitativasJuradoRegistradasEvent(contexto.entregable());
        eventPublisher.publish(evento);
        return evento;
    }
}
