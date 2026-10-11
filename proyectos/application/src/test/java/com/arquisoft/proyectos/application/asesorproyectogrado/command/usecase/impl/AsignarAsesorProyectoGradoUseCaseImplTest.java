package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesoresAsignadosContadorFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorYaAsignadoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.AsignarAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.event.AsesorProyectoGradoAsignadoEvent;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarAsesorProyectoGradoUseCaseImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;

    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;

    @Mock
    private AsesorYaAsignadoFinder asesorYaAsignadoFinder;

    @Mock
    private AsesoresAsignadosContadorFinder asesoresAsignadosContadorFinder;

    @Mock
    private AsignarAsesorProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarAsesorProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final AsesorDomain asesor = AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020",
            "Luis Diaz", "luis.diaz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final AsignacionAsesorProyectoGradoDomain entrada = AsignacionAsesorProyectoGradoDomain.crear(
            AsesorProyectoGradoDomain.crear(asesor.getId(), proyecto.getId()), proyecto.getCoordinador());

    private void stubFinders() {
        when(proyectoGradoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(asesorPorIdFinder.obtener(asesor.getId())).thenReturn(asesor);
        when(asesorYaAsignadoFinder.obtener(entrada)).thenReturn(false);
        when(asesoresAsignadosContadorFinder.obtener(proyecto.getId())).thenReturn(0L);
    }

    @Test
    void debeAsignarYPublicarConElContactoDelAsesor_cuandoLaAsignacionEsValida() {
        // Arrange
        stubFinders();

        // Act
        var id = useCase.ejecutar(entrada);

        // Assert
        assertThat(id).isEqualTo(entrada.getAsesorProyectoGrado().getId());

        var vinculo = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort).registrar(vinculo.capture());
        assertThat(vinculo.getValue().id()).isEqualTo(id);
        assertThat(vinculo.getValue().asesor()).isEqualTo(asesor.getId());
        assertThat(vinculo.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(vinculo.getValue().estadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO.getId());

        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(AsesorProyectoGradoAsignadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getAsesor()).isEqualTo(new ContactoAsesor("Luis Diaz", "luis.diaz@soyuco.edu.co"));
        });

        var orden = inOrder(proyectoGradoPorIdFinder, asesorPorIdFinder, asesorYaAsignadoFinder,
                asesoresAsignadosContadorFinder, validator, asesorProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_ASIGNANDO), eq(proyecto.getId()), eq(asesor.getId()));
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesor.getId());
        orden.verify(asesorYaAsignadoFinder, times(1)).obtener(entrada);
        orden.verify(asesoresAsignadosContadorFinder, times(1)).obtener(proyecto.getId());
        orden.verify(validator).validar(entrada, proyecto, asesor, false, 0L);
        orden.verify(asesorProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(eq(AsesorProyectoGradoKey.LOG_ASIGNADO), eq(proyecto.getId()), eq(asesor.getId()));
    }

    @Test
    void debeNoAsignarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders();
        doThrow(new CupoAsesoresExcedidoException(1))
                .when(validator).validar(eq(entrada), any(), any(), eq(false), anyLong());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(CupoAsesoresExcedidoException.class);
        verify(asesorProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(AsesorProyectoGradoKey.LOG_ASIGNADO), eq(proyecto.getId()), eq(asesor.getId()));
    }
}
