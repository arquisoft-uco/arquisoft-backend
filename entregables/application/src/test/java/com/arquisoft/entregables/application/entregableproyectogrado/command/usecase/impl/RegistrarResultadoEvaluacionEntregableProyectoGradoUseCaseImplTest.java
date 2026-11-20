package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarPaqueteResultadoEvaluacionValidator;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.RegistrarResultadoEvaluacionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.EstadoActualEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregablePorEntregableFinder;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoDeEntregableFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoAprobadoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoRechazadoEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarResultadoEvaluacionEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;
    @Mock
    private ProyectoGradoBloqueadoDeEntregableFinder proyectoGradoBloqueadoDeEntregableFinder;
    @Mock
    private EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    @Mock
    private EstadoActualEntregableProyectoGradoFinder estadoActualEntregableProyectoGradoFinder;
    @Mock
    private PaqueteEntregablePorEntregableFinder paqueteEntregablePorEntregableFinder;
    @Mock
    private RegistrarResultadoEvaluacionEntregableProyectoGradoValidator registrarValidator;
    @Mock
    private ConfirmarPaqueteResultadoEvaluacionValidator confirmarPaqueteValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarResultadoEvaluacionEntregableProyectoGradoUseCaseImpl useCase;

    private final UUID id = UtilUUID.generarNuevoUUID();
    private final UUID proyectoId = UtilUUID.generarNuevoUUID();
    private final EntregableProyectoGradoDomain entregable =
            EntregableProyectoGradoDomain.reconstruir(id, proyectoId, 3, UtilUUID.generarNuevoUUID());

    private EstadoEntregableProyectoGradoDomain estado(EstadoEntregable estado) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), id, estado, Instant.now());
    }

    private ProyectoGradoDomain proyecto(EstadoProyecto estado) {
        return ProyectoGradoDomain.reconstruir(proyectoId, "Sistema de riego", estado, null);
    }

    private PaqueteEntregableDomain paquete() {
        return PaqueteEntregableDomain.reconstruir(UtilUUID.generarNuevoUUID(), id, "entregables", "paquetes/a.zip",
                "a.zip", 10L, Instant.now());
    }

    private void prepararLecturas(EstadoEntregable estadoActual, EstadoProyecto estadoProyecto,
            PaqueteEntregableDomain paquete) {
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(proyecto(estadoProyecto));
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estado(estadoActual));
        when(paqueteEntregablePorEntregableFinder.obtener(id)).thenReturn(paquete);
    }

    @Test
    void debeRegistrarYPublicarAprobado_cuandoLaEvaluacionEsAprobada() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO");
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);
        var estadoActual = estado(EstadoEntregable.DISPONIBLE_EVALUACION);
        var paquete = paquete();
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(proyecto);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estadoActual);
        when(paqueteEntregablePorEntregableFinder.obtener(id)).thenReturn(paquete);

        // Act
        var salida = useCase.ejecutar(resultado);

        // Assert
        var orden = inOrder(proyectoGradoBloqueadoDeEntregableFinder, entregableProyectoGradoPorIdFinder,
                estadoActualEntregableProyectoGradoFinder, paqueteEntregablePorEntregableFinder, registrarValidator,
                confirmarPaqueteValidator, estadoEntregableProyectoGradoOutputPort, eventPublisher);
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        var captorEvento = ArgumentCaptor.forClass(DomainEvent.class);
        orden.verify(proyectoGradoBloqueadoDeEntregableFinder, times(1)).obtener(id);
        orden.verify(entregableProyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(estadoActualEntregableProyectoGradoFinder, times(1)).obtener(id);
        orden.verify(paqueteEntregablePorEntregableFinder, times(1)).obtener(id);
        orden.verify(registrarValidator).validar(resultado, entregable, estadoActual, proyecto);
        orden.verify(confirmarPaqueteValidator).validar(resultado, paquete);
        orden.verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        orden.verify(eventPublisher).publish(captorEvento.capture());
        assertThat(captorEstado.getValue().entregableProyectoGrado()).isEqualTo(id);
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo("APROBADO");
        assertThat(captorEvento.getValue()).isInstanceOfSatisfying(EntregableProyectoGradoAprobadoEvent.class, e -> {
            assertThat(e.getEntregableProyectoGrado()).isEqualTo(id);
            assertThat(e.getProyectoGrado()).isEqualTo(proyectoId);
            assertThat(e.getVersion()).isEqualTo(3);
            assertThat(e.getEstadoEntregable()).isEqualTo("APROBADO");
            assertThat(e.getEstadoEntregableNombre()).isEqualTo("Aprobado");
            assertThat(e.getBucket()).isEqualTo("entregables");
            assertThat(e.getClaveObjeto()).isEqualTo("paquetes/a.zip");
            assertThat(e.getNombreArchivo()).isEqualTo("a.zip");
        });
        assertThat(salida).isEqualTo(
                new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado(id, "APROBADO"));
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    void debePublicarAprobadoConObservaciones_cuandoElEntregableEstabaEnEvaluacion() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO_CON_OBSERVACIONES");
        prepararLecturas(EstadoEntregable.EN_EVALUACION, EstadoProyecto.ATRASADO, paquete());

        // Act
        var salida = useCase.ejecutar(resultado);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoAprobadoEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getEstadoEntregable()).isEqualTo("APROBADO_CON_OBSERVACIONES");
        assertThat(captor.getValue().getEstadoEntregableNombre()).isEqualTo("Aprobado Con Observaciones");
        assertThat(salida).isInstanceOf(RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado.class);
    }

    @Test
    void debeRegistrarYPublicarRechazado_cuandoLaEvaluacionEsRechazada() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "RECHAZADO");
        prepararLecturas(EstadoEntregable.DISPONIBLE_EVALUACION, EstadoProyecto.EN_PROCESO,
                PaqueteEntregableDomain.VACIO);

        // Act
        var salida = useCase.ejecutar(resultado);

        // Assert
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        var captorEvento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo("RECHAZADO");
        assertThat(captorEvento.getValue()).isInstanceOfSatisfying(EntregableProyectoGradoRechazadoEvent.class, e -> {
            assertThat(e.getEntregableProyectoGrado()).isEqualTo(id);
            assertThat(e.getProyectoGrado()).isEqualTo(proyectoId);
            assertThat(e.getVersion()).isEqualTo(3);
        });
        assertThat(salida).isEqualTo(
                new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado(id, "RECHAZADO"));
    }

    @Test
    void debeDescartarSinConfirmarPaqueteNiEscribir_cuandoElEstadoNoAdmiteResultado() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO");
        prepararLecturas(EstadoEntregable.APROBADO, EstadoProyecto.EN_PROCESO, PaqueteEntregableDomain.VACIO);

        // Act
        var salida = useCase.ejecutar(resultado);

        // Assert
        assertThat(salida).isEqualTo(new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado(
                id, "APROBADO", EstadoProyecto.EN_PROCESO.getId()));
        verifyNoInteractions(confirmarPaqueteValidator, eventPublisher);
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
    }

    @Test
    void debeDescartar_cuandoElProyectoEstaCerradoAunqueElEstadoAdmitaResultado() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO");
        prepararLecturas(EstadoEntregable.DISPONIBLE_EVALUACION, EstadoProyecto.CANCELADO, PaqueteEntregableDomain.VACIO);

        // Act
        var salida = useCase.ejecutar(resultado);

        // Assert
        assertThat(salida).isEqualTo(new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado(
                id, "DISPONIBLE_EVALUACION", EstadoProyecto.CANCELADO.getId()));
        verifyNoInteractions(confirmarPaqueteValidator, eventPublisher);
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
    }

    @Test
    void noDebeConfirmarPaqueteNiEscribir_cuandoElValidatorRechaza() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO");
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);
        var estadoActual = estado(EstadoEntregable.DISPONIBLE_EVALUACION);
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(proyecto);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estadoActual);
        when(paqueteEntregablePorEntregableFinder.obtener(id)).thenReturn(PaqueteEntregableDomain.VACIO);
        doThrow(new DomainException("rechazado", "COD"))
                .when(registrarValidator).validar(resultado, entregable, estadoActual, proyecto);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(resultado)).isInstanceOf(DomainException.class);
        verifyNoInteractions(confirmarPaqueteValidator, eventPublisher);
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
    }

    @Test
    void noDebeEscribirNiPublicar_cuandoElValidatorDelPaqueteRechaza() {
        // Arrange
        var resultado = ResultadoEvaluacionEntregableProyectoGradoDomain.crear(id, "APROBADO");
        prepararLecturas(EstadoEntregable.DISPONIBLE_EVALUACION, EstadoProyecto.EN_PROCESO,
                PaqueteEntregableDomain.VACIO);
        doThrow(new DomainException("sin paquete", "COD"))
                .when(confirmarPaqueteValidator).validar(resultado, PaqueteEntregableDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(resultado)).isInstanceOf(DomainException.class);
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
        verifyNoInteractions(eventPublisher);
    }
}
