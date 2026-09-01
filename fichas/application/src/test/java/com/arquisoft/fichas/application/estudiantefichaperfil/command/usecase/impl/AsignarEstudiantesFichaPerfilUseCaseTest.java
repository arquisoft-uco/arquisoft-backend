package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.event.EstudiantesFichaPerfilAsignadosEvent;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesFichaPerfilUseCaseTest {

    private static final String TITULO = "Sistema de gestión";
    private static final String NOMBRE = "Ana Gomez";
    private static final String EMAIL = "ana.gomez@soyuco.edu.co";

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private EstudiantesExistentesFinder estudiantesExistentesFinder;

    @Mock
    private EstudiantesFinder estudiantesFinder;

    @Mock
    private EstudiantesYaVinculadosFinder estudiantesYaVinculadosFinder;

    @Mock
    private EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;

    @Mock
    private AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstudiantesFichaPerfilUseCaseImpl asignarEstudiantesFichaPerfilUseCase;

    private final UUID estudiante = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.crear(TITULO, UUID.randomUUID());
    private final UUID fichaPerfil = ficha.getId();

    @Test
    void debeVincularCadaEstudiante_cuandoDatosValidos() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(1))
                .vincularEstudiante(entidadDe(relaciones.getRelaciones().getFirst()));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilFinder, estudiantesExistentesFinder,
                estudiantesYaVinculadosFinder, estudiantesVinculadosContadorFinder,
                asignarEstudiantesFichaPerfilValidator, estudianteFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilFinder).obtener(fichaPerfil);
        inOrder.verify(estudiantesExistentesFinder).obtener(List.of(estudiante));
        inOrder.verify(estudiantesYaVinculadosFinder).obtener(relaciones.getRelaciones());
        inOrder.verify(estudiantesVinculadosContadorFinder).obtener(fichaPerfil);
        inOrder.verify(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, ficha, List.of(estudiante), List.of(), 0L);
        inOrder.verify(estudianteFichaPerfilOutputPort)
                .vincularEstudiante(entidadDe(relaciones.getRelaciones().getFirst()));
    }

    @Test
    void debePublicarElEventoConElTituloYLosContactos_cuandoLosEstudiantesQuedanVinculados() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        ArgumentCaptor<EstudiantesFichaPerfilAsignadosEvent> captor =
                ArgumentCaptor.forClass(EstudiantesFichaPerfilAsignadosEvent.class);
        verify(eventPublisher).publish(captor.capture());

        EstudiantesFichaPerfilAsignadosEvent evento = captor.getValue();
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(evento.getTituloProyecto()).isEqualTo(TITULO);
        assertThat(evento.getEstudiantes()).containsExactly(new ContactoEstudiante(NOMBRE, EMAIL));
    }

    @Test
    void debePublicarDespuesDeVincular_cuandoElCasoDeUsoTermina() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        InOrder inOrder = inOrder(estudianteFichaPerfilOutputPort, eventPublisher);
        inOrder.verify(estudianteFichaPerfilOutputPort).vincularEstudiante(any());
        inOrder.verify(eventPublisher).publish(any(EstudiantesFichaPerfilAsignadosEvent.class));
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(FichaPerfilDomain.VACIO, List.of(estudiante), List.of(), 0L);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfil))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, FichaPerfilDomain.VACIO, List.of(estudiante), List.of(), 0L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoAlgunEstudianteNoExiste() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(), List.of(), 0L);
        doThrow(new EstudianteNoEncontradoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, ficha, List.of(), List.of(), 0L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(EstudianteNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoSeExcedeElCupo() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 5L);
        doThrow(new CupoEstudiantesExcedidoException(3))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, ficha, List.of(estudiante), List.of(), 5L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(ficha, List.of(estudiante), List.of(), 0L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estudianteFichaPerfilOutputPort)
                .vincularEstudiante(entidadDe(relaciones.getRelaciones().getFirst()));

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(InfrastructureException.class);

        verify(eventPublisher, never()).publish(any());
    }

    private void stubConsultas(FichaPerfilDomain fichaPerfilDomain, List<UUID> estudiantesExistentes,
                               List<UUID> yaVinculados, long vinculadosActuales) {
        when(fichaPerfilFinder.obtener(fichaPerfil)).thenReturn(presencia(fichaPerfilDomain));
        when(estudiantesExistentesFinder.obtener(List.of(estudiante))).thenReturn(estudiantesExistentes);
        when(estudiantesYaVinculadosFinder.obtener(any())).thenReturn(yaVinculados);
        when(estudiantesVinculadosContadorFinder.obtener(fichaPerfil)).thenReturn(vinculadosActuales);
        lenient().when(estudiantesFinder.obtener(List.of(estudiante))).thenReturn(
                List.of(EstudianteDomain.reconstruir(estudiante, "1001", NOMBRE, EMAIL)));
    }

    private static Optional<FichaPerfilDomain> presencia(FichaPerfilDomain fichaPerfilDomain) {
        return fichaPerfilDomain.esVacio() ? Optional.empty() : Optional.of(fichaPerfilDomain);
    }

    private AgregacionEstudiantesFichaPerfilDomain relaciones() {
        return AgregacionEstudiantesFichaPerfilDomain.crear(
                EstudianteFichaPerfilDomain.crear(fichaPerfil, List.of(estudiante)));
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static EstudianteFichaPerfilEntity entidadDe(EstudianteFichaPerfilDomain relacion) {
        return argThat(entity -> entity.fichaPerfilId().equals(relacion.getFichaPerfilId())
                && entity.estudianteId().equals(relacion.getEstudianteId()));
    }
}
