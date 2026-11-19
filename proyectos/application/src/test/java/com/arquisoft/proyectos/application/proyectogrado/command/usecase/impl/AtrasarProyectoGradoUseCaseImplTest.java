package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.AtrasarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoEstadoCambiadoEvent;
import com.arquisoft.shared.events.DomainEvent;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtrasarProyectoGradoUseCaseImplTest {

    @Mock
    private EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;

    @Mock
    private ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;

    @Mock
    private EstadoActualProyectoGradoFinder estadoActualProyectoGradoFinder;

    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;

    @Mock
    private EstudiantesVigentesDeProyectoGradoFinder estudiantesVigentesDeProyectoGradoFinder;

    @Mock
    private AsesoresAsignadosDeProyectoGradoFinder asesoresAsignadosDeProyectoGradoFinder;

    @Mock
    private AtrasarProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AtrasarProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(proyecto.getCoordinador(), "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final AtrasoProyectoGradoDomain entrada = AtrasoProyectoGradoDomain.crear(proyecto.getId());

    private EstudianteDomain estudiante(String nombre, String email) {
        return EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020", nombre, email,
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private AsesorDomain asesor(String nombre, String email) {
        return AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2015", nombre, email,
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private void stubFinders(EstadoProyectoGradoDomain estado, List<EstudianteDomain> estudiantes,
                             List<AsesorDomain> asesores) {
        when(proyectoGradoBloqueadoPorIdFinder.obtener(proyecto.getId())).thenReturn(proyecto);
        when(estadoActualProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estado);
        when(coordinadorPorIdFinder.obtener(proyecto.getCoordinador())).thenReturn(coordinador);
        when(estudiantesVigentesDeProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(estudiantes);
        when(asesoresAsignadosDeProyectoGradoFinder.obtener(proyecto.getId())).thenReturn(asesores);
    }

    @Test
    void debeRegistrarAtrasadoYPublicarEvento_cuandoEnProceso() {
        // Arrange
        var estadoActual = EstadoProyectoGradoDomain.crear(proyecto.getId());
        var ana = estudiante("Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var luis = estudiante("Luis Diaz", "luis.diaz@soyuco.edu.co");
        var eva = estudiante("Eva Mora", "eva.mora@soyuco.edu.co");
        var mario = asesor("Mario Peña", "mario.pena@uco.edu.co");
        stubFinders(estadoActual, List.of(ana, luis, eva), List.of(mario));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AtrasoProyectoGradoResult.Atrasado(proyecto.getId()));
        var fila = ArgumentCaptor.forClass(EstadoProyectoGradoEntity.class);
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(estadoProyectoGradoOutputPort).registrar(fila.capture());
        verify(eventPublisher).publish(evento.capture());
        assertThat(fila.getValue().estadoProyecto()).isEqualTo(EstadoProyecto.ATRASADO.getId());
        assertThat(fila.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoEstadoCambiadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(publicado.getEstadoAnterior()).isEqualTo(EstadoProyecto.EN_PROCESO.getId());
            assertThat(publicado.getEstadoNuevo()).isEqualTo(EstadoProyecto.ATRASADO.getId());
            assertThat(publicado.getMotivo()).isEmpty();
            assertThat(publicado.getCoordinador())
                    .isEqualTo(new ContactoCoordinador(coordinador.getId(), "Laura Mesa", "laura.mesa@uco.edu.co"));
            assertThat(publicado.getEstudiantes()).containsExactly(
                    new ContactoEstudiante(ana.getId(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co"),
                    new ContactoEstudiante(luis.getId(), "Luis Diaz", "luis.diaz@soyuco.edu.co"),
                    new ContactoEstudiante(eva.getId(), "Eva Mora", "eva.mora@soyuco.edu.co"));
            assertThat(publicado.getAsesores()).containsExactly(
                    new ContactoAsesor("Mario Peña", "mario.pena@uco.edu.co"));
        });

        var orden = inOrder(proyectoGradoBloqueadoPorIdFinder, estadoActualProyectoGradoFinder,
                coordinadorPorIdFinder, estudiantesVigentesDeProyectoGradoFinder,
                asesoresAsignadosDeProyectoGradoFinder, validator, estadoProyectoGradoOutputPort, eventPublisher);
        orden.verify(proyectoGradoBloqueadoPorIdFinder, times(1)).obtener(proyecto.getId());
        orden.verify(estadoActualProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(coordinadorPorIdFinder, times(1)).obtener(proyecto.getCoordinador());
        orden.verify(estudiantesVigentesDeProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(asesoresAsignadosDeProyectoGradoFinder, times(1)).obtener(proyecto.getId());
        orden.verify(validator).validar(entrada, proyecto, estadoActual, coordinador);
        orden.verify(estadoProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debeDevolverOmitidoSinPublicar_cuandoNoEstaEnProceso() {
        // Arrange
        var atrasado = EstadoProyectoGradoDomain.crearConEstado(proyecto.getId(), EstadoProyecto.ATRASADO);
        stubFinders(atrasado, List.of(), List.of());

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(
                new AtrasoProyectoGradoResult.Omitido(proyecto.getId(), EstadoProyecto.ATRASADO.getId()));
        verify(validator).validar(entrada, proyecto, atrasado, coordinador);
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzar_cuandoValidatorRechaza() {
        // Arrange
        var estadoActual = EstadoProyectoGradoDomain.crear(proyecto.getId());
        stubFinders(estadoActual, List.of(), List.of());
        doThrow(new CoordinadorNoEncontradoException(proyecto.getCoordinador()))
                .when(validator).validar(entrada, proyecto, estadoActual, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(CoordinadorNoEncontradoException.class);
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
