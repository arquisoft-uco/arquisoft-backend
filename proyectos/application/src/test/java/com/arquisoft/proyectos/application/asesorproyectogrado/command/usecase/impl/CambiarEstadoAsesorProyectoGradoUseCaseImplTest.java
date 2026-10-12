package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoVigenteFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.CambiarEstadoAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.event.AsesorProyectoGradoFinalizadoEvent;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.TransicionEstadoAsesorInvalidaException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoAsesorProyectoGradoUseCaseImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;

    @Mock
    private AsesorProyectoGradoVigenteFinder asesorProyectoGradoVigenteFinder;

    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;

    @Mock
    private CambiarEstadoAsesorProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoAsesorProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain estadoActual = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final AsesorDomain asesor = AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020",
            "Luis Diaz", "luis.diaz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final AsesorProyectoGradoDomain vinculo = AsesorProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), asesor.getId(), proyecto.getId(), EstadoAsesor.ASIGNADO,
            UtilFecha.generarInstanteActual());
    private final CambioEstadoAsesorProyectoGradoDomain entrada = CambioEstadoAsesorProyectoGradoDomain.crear(
            proyecto.getId(), asesor.getId(), proyecto.getCoordinador(), "FINALIZADO");

    private void stubFinders() {
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(asesorPorIdFinder.obtener(asesor.getId())).thenReturn(asesor);
        when(asesorProyectoGradoVigenteFinder.obtener(entrada)).thenReturn(vinculo);
    }

    @Test
    void debeRegistrarFilaNuevaYPublicar_enOrden() {
        // Arrange
        stubFinders();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var fila = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(asesorProyectoGradoOutputPort).registrar(fila.capture());
        verify(eventPublisher).publish(evento.capture());
        assertThat(fila.getValue().estadoAsesor()).isEqualTo("FINALIZADO");
        assertThat(fila.getValue().asesor()).isEqualTo(asesor.getId());
        assertThat(fila.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(fila.getValue().id()).isNotEqualTo(vinculo.getId());
        assertThat(fila.getValue().fechaAsignado()).isAfter(vinculo.getFechaAsignado().minusSeconds(1));
        assertThat(evento.getValue()).isInstanceOfSatisfying(AsesorProyectoGradoFinalizadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getAsesor()).isEqualTo(new ContactoAsesor("Luis Diaz", "luis.diaz@soyuco.edu.co"));
        });

        var orden = inOrder(proyectoGradoPorIdFinder, estadoActualProyectoGradoFinder, asesorPorIdFinder, asesorProyectoGradoVigenteFinder,
                validator, asesorProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_CAMBIANDO_ESTADO),
                eq(proyecto.getId()), eq(asesor.getId()), eq("FINALIZADO"));
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estadoActualProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesor.getId());
        orden.verify(asesorProyectoGradoVigenteFinder, times(1)).obtener(entrada);
        orden.verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO),
                eq(true), eq(true), eq(true), eq(true));
        orden.verify(validator).validar(entrada, proyecto, estadoActual, asesor, vinculo);
        orden.verify(asesorProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(asesor.getId()), eq("FINALIZADO"));
    }

    @Test
    void debeNoRegistrarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders();
        doThrow(new TransicionEstadoAsesorInvalidaException(EstadoAsesor.FINALIZADO, EstadoAsesor.FINALIZADO))
                .when(validator).validar(entrada, proyecto, estadoActual, asesor, vinculo);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
        verify(asesorProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(asesor.getId()), eq("FINALIZADO"));
    }

    @Test
    void debeConsultarFindersYRechazar_cuandoElVinculoEsVacio() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(asesorPorIdFinder.obtener(asesor.getId())).thenReturn(asesor);
        when(asesorProyectoGradoVigenteFinder.obtener(entrada)).thenReturn(AsesorProyectoGradoDomain.VACIO);
        doThrow(new AsesorProyectoGradoNoEncontradoException(asesor.getId(), proyecto.getId()))
                .when(validator).validar(entrada, proyecto, estadoActual, asesor, AsesorProyectoGradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
        verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO),
                eq(true), eq(true), eq(true), eq(false));
        verify(asesorProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
