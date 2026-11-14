package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.UltimoEntregableProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.CambiarEstadoProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoEstadoCambiadoEvent;
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
    private AsesoresAsignadosDeProyectoGradoFinder asesoresAsignadosDeProyectoGradoFinder;

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

    private AsesorDomain asesor(String nombre, String email) {
        return AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2015", nombre, email,
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private void stubFinders(List<EstudianteDomain> estudiantes, List<AsesorDomain> asesores) {
        stubFinders(estadoActual, estudiantes, asesores);
    }

    private void stubFinders(EstadoProyectoGradoDomain estado, List<EstudianteDomain> estudiantes,
                             List<AsesorDomain> asesores) {
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estado);
        when(asesorAsignadoProyectoGradoFinder.obtener(entrada)).thenReturn(true);
        when(ultimoEntregableProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(ultimoEntregable);
        when(estudiantesVigentesDeProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estudiantes);
        when(asesoresAsignadosDeProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(asesores);
    }

    @Test
    void debeRegistrarEstadoYPublicar_cuandoElValidatorAprueba() {
        // Arrange
        var ana = estudiante("Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var luis = estudiante("Luis Diaz", "luis.diaz@soyuco.edu.co");
        var eva = estudiante("Eva Mora", "eva.mora@soyuco.edu.co");
        var mario = asesor("Mario Peña", "mario.pena@uco.edu.co");
        stubFinders(List.of(ana, luis, eva), List.of(mario));

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var fila = ArgumentCaptor.forClass(EstadoProyectoGradoEntity.class);
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(estadoProyectoGradoOutputPort).registrar(fila.capture());
        verify(eventPublisher).publish(evento.capture());
        assertThat(fila.getValue().estadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO.getId());
        assertThat(fila.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoEstadoCambiadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getEstadoAnterior()).isEqualTo(EstadoProyecto.EN_PROCESO.getId());
            assertThat(publicado.getEstadoNuevo()).isEqualTo(EstadoProyecto.FINALIZADO.getId());
            assertThat(publicado.getMotivo()).isEmpty();
            assertThat(publicado.getEstudiantes()).containsExactly(
                    new ContactoEstudiante(ana.getId(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co"),
                    new ContactoEstudiante(luis.getId(), "Luis Diaz", "luis.diaz@soyuco.edu.co"),
                    new ContactoEstudiante(eva.getId(), "Eva Mora", "eva.mora@soyuco.edu.co"));
            assertThat(publicado.getAsesores()).containsExactly(
                    new ContactoAsesor("Mario Peña", "mario.pena@uco.edu.co"));
        });

        var orden = inOrder(proyectoGradoBloqueadoPorIdFinder, estadoActualProyectoGradoFinder,
                asesorAsignadoProyectoGradoFinder, ultimoEntregableProyectoGradoFinder,
                estudiantesVigentesDeProyectoGradoFinder, asesoresAsignadosDeProyectoGradoFinder, validator,
                estadoProyectoGradoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(eq(ProyectoGradoKey.LOG_CAMBIANDO_ESTADO),
                eq(proyecto.getId()), eq(entrada.getAsesor()), eq("FINALIZADO"));
        orden.verify(proyectoGradoBloqueadoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estadoActualProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesorAsignadoProyectoGradoFinder, times(1)).obtener(entrada);
        orden.verify(ultimoEntregableProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estudiantesVigentesDeProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesoresAsignadosDeProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_VERIFICACION_CAMBIO_ESTADO),
                eq(true), eq(true), eq(true), eq(true), eq(3), eq(1));
        orden.verify(validator).validar(entrada, proyecto, estadoActual, true, ultimoEntregable);
        orden.verify(estadoProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(eq(ProyectoGradoKey.LOG_ESTADO_CAMBIADO),
                eq(proyecto.getId()), eq(entrada.getAsesor()), eq("FINALIZADO"));
    }

    @Test
    void debePublicarEstadoAnteriorAtrasado_cuandoFinalizaDesdeAtrasado() {
        // Arrange
        var atrasado = EstadoProyectoGradoDomain.crearConEstado(proyecto.getId(), EstadoProyecto.ATRASADO);
        stubFinders(atrasado, List.of(), List.of());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(evento.capture());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoEstadoCambiadoEvent.class, publicado -> {
            assertThat(publicado.getEstadoAnterior()).isEqualTo(EstadoProyecto.ATRASADO.getId());
            assertThat(publicado.getEstadoNuevo()).isEqualTo(EstadoProyecto.FINALIZADO.getId());
            assertThat(publicado.getEstudiantes()).isEmpty();
            assertThat(publicado.getAsesores()).isEmpty();
        });
        verify(estadoProyectoGradoOutputPort).registrar(any());
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders(List.of(), List.of());
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
