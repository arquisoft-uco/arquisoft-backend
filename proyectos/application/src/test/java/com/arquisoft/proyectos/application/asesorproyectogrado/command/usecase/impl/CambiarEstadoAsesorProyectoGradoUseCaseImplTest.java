package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorIdYProyectoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.CambiarEstadoAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.event.AsesorProyectoGradoFinalizadoEvent;
import com.arquisoft.proyectos.domain.asesor.exception.AsesorNoEncontradoException;
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
    private AsesorProyectoGradoPorIdYProyectoFinder asesorProyectoGradoPorIdYProyectoFinder;

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
    private final AsesorDomain asesor = AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020",
            "Luis Diaz", "luis.diaz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final AsesorProyectoGradoDomain vinculo = AsesorProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), asesor.getId(), proyecto.getId(), EstadoAsesor.ASIGNADO,
            UtilFecha.generarInstanteActual());
    private final CambioEstadoAsesorProyectoGradoDomain entrada = CambioEstadoAsesorProyectoGradoDomain.crear(
            proyecto.getId(), vinculo.getId(), proyecto.getCoordinador(), "FINALIZADO");

    private void stubFinders() {
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(asesorProyectoGradoPorIdYProyectoFinder.obtener(entrada)).thenReturn(vinculo);
    }

    @Test
    void debeCambiarYPublicarConElContactoDelAsesor_cuandoElCambioEsValido() {
        // Arrange
        stubFinders();
        when(asesorPorIdFinder.obtener(asesor.getId())).thenReturn(asesor);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(AsesorProyectoGradoFinalizadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getAsesor()).isEqualTo(new ContactoAsesor("Luis Diaz", "luis.diaz@soyuco.edu.co"));
        });

        var orden = inOrder(proyectoGradoPorIdFinder, asesorProyectoGradoPorIdYProyectoFinder, asesorPorIdFinder,
                validator, asesorProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_CAMBIANDO_ESTADO),
                eq(proyecto.getId()), eq(vinculo.getId()), eq("FINALIZADO"));
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesorProyectoGradoPorIdYProyectoFinder, times(1)).obtener(entrada);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesor.getId());
        orden.verify(logger).debug(eq(AsesorProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO),
                eq(true), eq(true), eq(true));
        orden.verify(validator).validar(entrada, proyecto, vinculo, asesor);
        orden.verify(asesorProyectoGradoOutputPort).actualizarEstado(vinculo.getId(), "FINALIZADO");
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(vinculo.getId()), eq("FINALIZADO"));
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders();
        when(asesorPorIdFinder.obtener(asesor.getId())).thenReturn(AsesorDomain.VACIO);
        doThrow(new AsesorNoEncontradoException(asesor.getId()))
                .when(validator).validar(entrada, proyecto, vinculo, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorNoEncontradoException.class);
        verify(asesorProyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(AsesorProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(vinculo.getId()), eq("FINALIZADO"));
    }

    @Test
    void debeConsultarAsesorVacio_yRechazar_cuandoElVinculoEsVacio() {
        // Arrange
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(asesorProyectoGradoPorIdYProyectoFinder.obtener(entrada)).thenReturn(AsesorProyectoGradoDomain.VACIO);
        when(asesorPorIdFinder.obtener(UtilUUID.obtenerUUIDPorDefecto())).thenReturn(AsesorDomain.VACIO);
        doThrow(new AsesorProyectoGradoNoEncontradoException(vinculo.getId(), proyecto.getId()))
                .when(validator).validar(entrada, proyecto, AsesorProyectoGradoDomain.VACIO, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
        verify(asesorPorIdFinder, times(1)).obtener(UtilUUID.obtenerUUIDPorDefecto());
        verify(asesorProyectoGradoOutputPort, never()).actualizarEstado(any(), any());
        verify(eventPublisher, never()).publish(any());
    }
}
