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
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder.DestinatariosEvaluacionFinder;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.RegistroEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event.EvaluacionesCualitativasJuradoRegistradasEvent;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event.EvaluacionesCualitativasJuradoRegistradasEvent.DatosLoteRegistrado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ContactoEstudianteEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final DestinatariosEvaluacionFinder destinatariosEvaluacionFinder;
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

        ContextoRegistroEvaluacionJuradoEntity contexto =
                validarAccesoYObtenerContexto(evaluacionJurado, registro.getActor());

        validarContenidoDelLote(registro, evaluacionJurado);

        iniciarEvaluacionUseCase.ejecutar(InicioEvaluacionDomain.crear(
                contexto.evaluacion(), EstadoEvaluacion.desde(contexto.estado())));

        List<DestinatarioEvaluacionEntity> destinatarios =
                destinatariosEvaluacionFinder.obtener(contexto.entregable());

        var entidades = registro.getEvaluaciones().stream()
                .map(EvaluacionCualitativaJuradoMapper::toEntity)
                .toList();
        evaluacionCualitativaJuradoOutputPort.registrarTodas(entidades);

        var evento = publicarEvento(evaluacionJurado, contexto, entidades.size(), destinatarios);

        logger.info(EvaluacionCualitativaJuradoKey.LOG_LOTE_REGISTRADO,
                evaluacionJurado, entidades.size(), evento.getIdEvento());
    }

    private ContextoRegistroEvaluacionJuradoEntity validarAccesoYObtenerContexto(
            UUID evaluacionJurado, UUID actor) {
        var contextoOpt = contextoRegistroEvaluacionJuradoFinder.obtener(evaluacionJurado);
        boolean existeEvaluacionJurado = contextoOpt.isPresent();
        UUID propietario = contextoOpt.map(ContextoRegistroEvaluacionJuradoEntity::jurado).orElse(null);

        validator.validarAcceso(
                new ExistenciaEvaluacionJurado(evaluacionJurado, existeEvaluacionJurado),
                new PropiedadEvaluacionJurado(actor, propietario));

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
            UUID evaluacionJurado,
            ContextoRegistroEvaluacionJuradoEntity contexto,
            int cantidad,
            List<DestinatarioEvaluacionEntity> destinatarios) {
        var contactos = destinatarios.stream()
                .map(destinatario -> new ContactoEstudianteEvaluacion(
                        destinatario.estudiante(), destinatario.email()))
                .toList();

        var evento = new EvaluacionesCualitativasJuradoRegistradasEvent(
                new DatosLoteRegistrado(
                        evaluacionJurado,
                        contexto.evaluacion(),
                        contexto.entregable(),
                        contexto.jurado(),
                        contexto.proyecto(),
                        contexto.versionEntregable(),
                        cantidad),
                contactos);
        eventPublisher.publish(evento);
        return evento;
    }
}
