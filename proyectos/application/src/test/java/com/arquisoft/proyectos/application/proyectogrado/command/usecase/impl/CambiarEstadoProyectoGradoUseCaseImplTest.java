package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.UltimoEntregableProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.CambiarEstadoProyectoGradoValidator;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoFinalizadoEvent;
import com.arquisoft.proyectos.domain.proyectogrado.exception.TransicionEstadoProyectoNoPermitidaException;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

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
class CambiarEstadoProyectoGradoUseCaseImplTest {

    @Mock
    private EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;

    @Mock
    private EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;

    @Mock
    private AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;

    @Mock
    private UltimoEntregableProyectoGradoFinder ultimoEntregableProyectoGradoFinder;

    @Mock
    private EstudiantesVigentesDeProyectoGradoFinder estudiantesVigentesDeProyectoGradoFinder;

    @Mock
    private CambiarEstadoProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain estadoActual = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final EntregableProyectoGradoDomain ultimoEntregable = EntregableProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), 2, EstadoEntregable.CERRADO,
            Instant.parse("2026-11-01T10:00:00Z"));
    private final CambioEstadoProyectoGradoDomain entrada = CambioEstadoProyectoGradoDomain.crear(
            proyecto.getId(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

    private EstudianteDomain estudiante(String nombre, String email) {
        return EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020", nombre, email,
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private void stubFinders(List<EstudianteDomain> estudiantes) {
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estadoActual);
        when(asesorAsignadoProyectoGradoFinder.obtener(entrada)).thenReturn(true);
        when(ultimoEntregableProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(ultimoEntregable);
        when(estudiantesVigentesDeProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estudiantes);
    }

    @Test
    void debeRegistrarEstadoYPublicar_cuandoElValidatorAprueba() {
        // Arrange
        var ana = estudiante("Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var luis = estudiante("Luis Diaz", "luis.diaz@soyuco.edu.co");
        var eva = estudiante("Eva Mora", "eva.mora@soyuco.edu.co");
        stubFinders(List.of(ana, luis, eva));

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var fila = ArgumentCaptor.forClass(EstadoProyectoGradoEntity.class);
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(estadoProyectoGradoOutputPort).registrar(fila.capture());
        verify(eventPublisher).publish(evento.capture());
        assertThat(fila.getValue().estadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO.getId());
        assertThat(fila.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoFinalizadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getEstudiantes()).containsExactly(
                    new ContactoEstudiante(ana.getId(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co"),
                    new ContactoEstudiante(luis.getId(), "Luis Diaz", "luis.diaz@soyuco.edu.co"),
                    new ContactoEstudiante(eva.getId(), "Eva Mora", "eva.mora@soyuco.edu.co"));
        });

        var orden = inOrder(proyectoGradoBloqueadoPorIdFinder, estadoActualProyectoGradoFinder,
                asesorAsignadoProyectoGradoFinder, ultimoEntregableProyectoGradoFinder,
                estudiantesVigentesDeProyectoGradoFinder, validator, estadoProyectoGradoOutputPort,
                eventPublisher, logger);
        orden.verify(logger).info(eq(ProyectoGradoKey.LOG_CAMBIANDO_ESTADO),
                eq(proyecto.getId()), eq(entrada.getAsesor()), eq("FINALIZADO"));
        orden.verify(proyectoGradoBloqueadoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estadoActualProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesorAsignadoProyectoGradoFinder, times(1)).obtener(entrada);
        orden.verify(ultimoEntregableProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estudiantesVigentesDeProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO),
                eq(true), eq(true), eq(true), eq(true), eq(3));
        orden.verify(validator).validar(entrada, proyecto, estadoActual, true, ultimoEntregable);
        orden.verify(estadoProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(eq(ProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(entrada.getAsesor()), eq("FINALIZADO"));
    }

    @Test
    void debePublicarConListaVacia_cuandoNoHayEstudiantesVigentes() {
        // Arrange
        stubFinders(List.of());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoFinalizadoEvent.class,
                publicado -> assertThat(publicado.getEstudiantes()).isEmpty());
        verify(estadoProyectoGradoOutputPort).registrar(any());
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders(List.of());
        doThrow(new TransicionEstadoProyectoNoPermitidaException(proyecto.getId(), proyecto.getTituloProyecto(),
                EstadoProyecto.FINALIZADO, EstadoProyecto.FINALIZADO))
                .when(validator).validar(entrada, proyecto, estadoActual, true, ultimoEntregable);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(TransicionEstadoProyectoNoPermitidaException.class);
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(ProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(entrada.getAsesor()), eq("FINALIZADO"));
    }
}
