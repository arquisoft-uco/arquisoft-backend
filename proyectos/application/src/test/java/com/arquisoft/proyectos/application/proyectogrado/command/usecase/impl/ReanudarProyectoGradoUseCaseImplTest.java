package com.arquisoft.proyectos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesoresAsignadosDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.EstadoActualProyectoGradoFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantesVigentesDeProyectoGradoFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.validator.ReanudarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
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
import java.time.LocalDate;
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
class ReanudarProyectoGradoUseCaseImplTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 19);

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
    private ReanudarProyectoGradoValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ReanudarProyectoGradoUseCaseImpl useCase;

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(proyecto.getCoordinador(), "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final EstadoProyectoGradoDomain atrasado = EstadoProyectoGradoDomain.crearConEstado(
            proyecto.getId(), EstadoProyecto.ATRASADO);

    private ReanudacionProyectoGradoDomain reanudacion(LocalDate fechaFin) {
        return ReanudacionProyectoGradoDomain.crear(proyecto.getId(), fechaFin, HOY);
    }

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
    void debeRegistrarEnProcesoYPublicarEvento_cuandoAtrasadoYPlazoVigente() {
        // Arrange
        var entrada = reanudacion(HOY);
        var ana = estudiante("Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var luis = estudiante("Luis Diaz", "luis.diaz@soyuco.edu.co");
        var eva = estudiante("Eva Mora", "eva.mora@soyuco.edu.co");
        var mario = asesor("Mario Peña", "mario.pena@uco.edu.co");
        stubFinders(atrasado, List.of(ana, luis, eva), List.of(mario));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new ReanudacionProyectoGradoResult.Reanudado(proyecto.getId()));
        var fila = ArgumentCaptor.forClass(EstadoProyectoGradoEntity.class);
        var evento = ArgumentCaptor.forClass(DomainEvent.class);
        verify(estadoProyectoGradoOutputPort).registrar(fila.capture());
        verify(eventPublisher).publish(evento.capture());
        assertThat(fila.getValue().estadoProyecto()).isEqualTo(EstadoProyecto.EN_PROCESO.getId());
        assertThat(fila.getValue().proyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(evento.getValue()).isInstanceOfSatisfying(ProyectoGradoEstadoCambiadoEvent.class, publicado -> {
            assertThat(publicado.getProyectoGradoId()).isEqualTo(proyecto.getId());
            assertThat(publicado.getEstadoAnterior()).isEqualTo(EstadoProyecto.ATRASADO.getId());
            assertThat(publicado.getEstadoNuevo()).isEqualTo(EstadoProyecto.EN_PROCESO.getId());
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
        orden.verify(validator).validar(entrada, proyecto, atrasado, coordinador);
        orden.verify(estadoProyectoGradoOutputPort).registrar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debeDevolverOmitido_cuandoNoEstaAtrasado() {
        // Arrange
        var enProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());
        stubFinders(enProceso, List.of(), List.of());

        // Act
        var resultado = useCase.ejecutar(reanudacion(HOY.plusDays(10)));

        // Assert
        assertThat(resultado).isEqualTo(new ReanudacionProyectoGradoResult.Omitido(
                proyecto.getId(), EstadoProyecto.EN_PROCESO.getId(), true));
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeDevolverOmitido_cuandoPlazoVencido() {
        // Arrange
        stubFinders(atrasado, List.of(), List.of());

        // Act
        var resultado = useCase.ejecutar(reanudacion(HOY.minusDays(1)));

        // Assert
        assertThat(resultado).isEqualTo(new ReanudacionProyectoGradoResult.Omitido(
                proyecto.getId(), EstadoProyecto.ATRASADO.getId(), false));
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzar_cuandoValidatorRechaza() {
        // Arrange
        var entrada = reanudacion(HOY);
        stubFinders(atrasado, List.of(), List.of());
        doThrow(new CoordinadorNoEncontradoException(proyecto.getCoordinador()))
                .when(validator).validar(entrada, proyecto, atrasado, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isInstanceOf(CoordinadorNoEncontradoException.class);
        verify(estadoProyectoGradoOutputPort, never()).registrar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
