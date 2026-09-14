package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.CriteriosCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.ItemsEvaluacionCualitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model.CriterioItemsEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.RegistrarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemsCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder.DestinatariosEvaluacionFinder;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.RegistroEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event.EvaluacionesCualitativasJuradoRegistradasEvent;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.ItemsCualitativosJuradoNoEncontradosException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionesCualitativasJuradoUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionJuradoFinder contextoFinder;

    @Mock
    private ItemsCualitativosJuradoExistentesFinder itemsFinder;

    @Mock
    private CriteriosCualitativosJuradoExistentesFinder criteriosFinder;

    @Mock
    private ItemsEvaluacionCualitativaJuradoRegistradosFinder itemsRegistradosFinder;

    @Mock
    private DestinatariosEvaluacionFinder destinatariosFinder;

    @Mock
    private RegistrarEvaluacionesCualitativasJuradoValidator validator;

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @Mock
    private IniciarEvaluacionUseCase iniciarEvaluacionUseCase;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarEvaluacionesCualitativasJuradoUseCaseImpl useCase;

    @Test
    void debeRegistrarIniciarEvaluacionYPublicarEvento_cuandoEstadoEsPendiente() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID evaluacion = UUID.randomUUID();
        UUID entregable = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var registro = registroValido(evaluacionJurado, jurado, item, criterio);
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, evaluacion, jurado, "PENDIENTE", entregable, "Proyecto X", 3);
        var destinatarios = List.of(new DestinatarioEvaluacionEntity(estudiante, "estudiante@uco.edu.co"));

        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(Optional.of(contexto));
        when(itemsFinder.obtener(anySet())).thenReturn(Set.of(item));
        when(criteriosFinder.obtener(anySet())).thenReturn(Set.of(criterio));
        when(itemsRegistradosFinder.obtener(any(CriterioItemsEvaluacion.class))).thenReturn(Set.of());
        when(destinatariosFinder.obtener(entregable)).thenReturn(destinatarios);

        // Act
        useCase.ejecutar(registro);

        // Assert
        InOrder orden = inOrder(contextoFinder, validator, iniciarEvaluacionUseCase, outputPort, eventPublisher);
        orden.verify(contextoFinder).obtener(evaluacionJurado);
        orden.verify(validator).validarAcceso(any(), any());
        orden.verify(validator).validarContenido(any(), any(), any());
        orden.verify(iniciarEvaluacionUseCase).ejecutar(any(InicioEvaluacionDomain.class));
        orden.verify(outputPort).registrarTodas(any());
        orden.verify(eventPublisher).publish(any());

        verify(itemsFinder).obtener(Set.of(item));
        verify(criteriosFinder).obtener(Set.of(criterio));
        verify(itemsRegistradosFinder).obtener(new CriterioItemsEvaluacion(evaluacionJurado, Set.of(item)));

        ArgumentCaptor<List<EvaluacionCualitativaJuradoEntity>> entidadesCaptor = ArgumentCaptor.forClass(List.class);
        verify(outputPort).registrarTodas(entidadesCaptor.capture());
        assertThat(entidadesCaptor.getValue()).hasSize(1);
        assertThat(entidadesCaptor.getValue().get(0).item()).isEqualTo(item);
        assertThat(entidadesCaptor.getValue().get(0).criterio()).isEqualTo(criterio);

        ArgumentCaptor<EvaluacionesCualitativasJuradoRegistradasEvent> eventoCaptor =
                ArgumentCaptor.forClass(EvaluacionesCualitativasJuradoRegistradasEvent.class);
        verify(eventPublisher).publish(eventoCaptor.capture());
        EvaluacionesCualitativasJuradoRegistradasEvent evento = eventoCaptor.getValue();
        assertThat(evento.getEvaluacionJuradoId()).isEqualTo(evaluacionJurado);
        assertThat(evento.getEvaluacionId()).isEqualTo(evaluacion);
        assertThat(evento.getEntregableId()).isEqualTo(entregable);
        assertThat(evento.getCantidad()).isEqualTo(1);
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThat(evento.getEstudiantes().get(0).estudiante()).isEqualTo(estudiante);
        assertThat(evento.getEstudiantes().get(0).email()).isEqualTo("estudiante@uco.edu.co");

        verify(logger).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(1));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionJurado), eq(1), eq(1));
        verify(logger).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(1), eq(evento.getIdEvento()));
    }

    @Test
    void debeRegistrarYPublicarEvento_cuandoLaEvaluacionYaEstaEnProgreso() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var registro = registroValido(evaluacionJurado, jurado, item, criterio);
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, UUID.randomUUID(), jurado, "EN_PROGRESO", UUID.randomUUID(), "Proyecto X", 1);

        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(Optional.of(contexto));
        when(itemsFinder.obtener(anySet())).thenReturn(Set.of(item));
        when(criteriosFinder.obtener(anySet())).thenReturn(Set.of(criterio));
        when(itemsRegistradosFinder.obtener(any(CriterioItemsEvaluacion.class))).thenReturn(Set.of());
        when(destinatariosFinder.obtener(any())).thenReturn(List.of());

        // Act
        useCase.ejecutar(registro);

        // Assert
        ArgumentCaptor<InicioEvaluacionDomain> inicioCaptor = ArgumentCaptor.forClass(InicioEvaluacionDomain.class);
        verify(iniciarEvaluacionUseCase).ejecutar(inicioCaptor.capture());
        assertThat(inicioCaptor.getValue().getEstadoActual().getId()).isEqualTo("EN_PROGRESO");
        verify(outputPort).registrarTodas(any());
        verify(eventPublisher).publish(any());
    }

    @Test
    void debeAbortarSinConsultarContenido_cuandoValidarAccesoRechaza() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var registro = registroValido(evaluacionJurado, jurado, item, criterio);
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, UUID.randomUUID(), UUID.randomUUID(), "PENDIENTE",
                UUID.randomUUID(), "Proyecto X", 1);
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(Optional.of(contexto));
        doThrow(new EvaluacionJuradoNoPerteneceJuradoException()).when(validator).validarAcceso(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
        verify(itemsFinder, never()).obtener(any());
        verify(iniciarEvaluacionUseCase, never()).ejecutar(any());
        verify(outputPort, never()).registrarTodas(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarSinIniciarEvaluacion_cuandoValidarContenidoRechaza() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var registro = registroValido(evaluacionJurado, jurado, item, criterio);
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, UUID.randomUUID(), jurado, "PENDIENTE", UUID.randomUUID(), "Proyecto X", 1);
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(Optional.of(contexto));
        when(itemsFinder.obtener(anySet())).thenReturn(Set.of());
        when(criteriosFinder.obtener(anySet())).thenReturn(Set.of(criterio));
        when(itemsRegistradosFinder.obtener(any(CriterioItemsEvaluacion.class))).thenReturn(Set.of());
        doThrow(new ItemsCualitativosJuradoNoEncontradosException(Set.of(item)))
                .when(validator).validarContenido(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(ItemsCualitativosJuradoNoEncontradosException.class);
        verify(iniciarEvaluacionUseCase, never()).ejecutar(any());
        verify(destinatariosFinder, never()).obtener(any());
        verify(outputPort, never()).registrarTodas(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarPublicacion_cuandoLaPersistenciaDelLoteFalla() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var registro = registroValido(evaluacionJurado, jurado, item, criterio);
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, UUID.randomUUID(), jurado, "PENDIENTE", UUID.randomUUID(), "Proyecto X", 1);
        RuntimeException fallo = new RuntimeException("fallo de persistencia");
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(Optional.of(contexto));
        when(itemsFinder.obtener(anySet())).thenReturn(Set.of(item));
        when(criteriosFinder.obtener(anySet())).thenReturn(Set.of(criterio));
        when(itemsRegistradosFinder.obtener(any(CriterioItemsEvaluacion.class))).thenReturn(Set.of());
        when(destinatariosFinder.obtener(any())).thenReturn(List.of());
        doThrow(fallo).when(outputPort).registrarTodas(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro)).isSameAs(fallo);
        verify(eventPublisher, never()).publish(any());
    }

    private static RegistroEvaluacionesCualitativasJuradoDomain registroValido(
            UUID evaluacionJurado, UUID actor, UUID item, UUID criterio) {
        var evaluacion = EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, item, criterio);
        return RegistroEvaluacionesCualitativasJuradoDomain.crear(actor, List.of(evaluacion));
    }
}
