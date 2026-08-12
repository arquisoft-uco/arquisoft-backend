package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesVinculadosContadorFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.EstudiantesYaVinculadosFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesFichaPerfilUseCaseTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private EstudiantesExistentesFinder estudiantesExistentesFinder;

    @Mock
    private EstudiantesYaVinculadosFinder estudiantesYaVinculadosFinder;

    @Mock
    private EstudiantesVinculadosContadorFinder estudiantesVinculadosContadorFinder;

    @Mock
    private AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private AsignarEstudiantesFichaPerfilUseCaseImpl asignarEstudiantesFichaPerfilUseCase;

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();

    @Test
    void debeVincularCadaEstudiante_cuandoDatosValidos() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(true, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(1)).vincularEstudiante(relaciones.getFirst());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(true, List.of(estudiante), List.of(), 0L);

        // Act
        asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, estudiantesExistentesFinder,
                estudiantesYaVinculadosFinder, estudiantesVinculadosContadorFinder,
                asignarEstudiantesFichaPerfilValidator, estudianteFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(fichaPerfil);
        inOrder.verify(estudiantesExistentesFinder).obtener(List.of(estudiante));
        inOrder.verify(estudiantesYaVinculadosFinder).obtener(relaciones);
        inOrder.verify(estudiantesVinculadosContadorFinder).obtener(fichaPerfil);
        inOrder.verify(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, true, List.of(estudiante), List.of(), 0L);
        inOrder.verify(estudianteFichaPerfilOutputPort).vincularEstudiante(relaciones.getFirst());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(false, List.of(estudiante), List.of(), 0L);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfil))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, false, List.of(estudiante), List.of(), 0L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoAlgunEstudianteNoExiste() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(true, List.of(), List.of(), 0L);
        doThrow(new EstudianteNoEncontradoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, true, List.of(), List.of(), 0L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(EstudianteNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoSeExcedeElCupo() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(true, List.of(estudiante), List.of(), 5L);
        doThrow(new CupoEstudiantesExcedidoException(3))
                .when(asignarEstudiantesFichaPerfilValidator)
                .validar(relaciones, true, List.of(estudiante), List.of(), 5L);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).vincularEstudiante(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var relaciones = relaciones();
        stubConsultas(true, List.of(estudiante), List.of(), 0L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estudianteFichaPerfilOutputPort).vincularEstudiante(relaciones.getFirst());

        // Act & Assert
        assertThatThrownBy(() -> asignarEstudiantesFichaPerfilUseCase.ejecutar(relaciones))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultas(boolean fichaExiste, List<UUID> estudiantesExistentes,
                               List<UUID> yaVinculados, long vinculadosActuales) {
        when(fichaPerfilExisteFinder.obtener(fichaPerfil)).thenReturn(fichaExiste);
        when(estudiantesExistentesFinder.obtener(List.of(estudiante))).thenReturn(estudiantesExistentes);
        when(estudiantesYaVinculadosFinder.obtener(any())).thenReturn(yaVinculados);
        when(estudiantesVinculadosContadorFinder.obtener(fichaPerfil)).thenReturn(vinculadosActuales);
    }

    private List<EstudianteFichaPerfilDomain> relaciones() {
        return EstudianteFichaPerfilDomain.crear(fichaPerfil, List.of(estudiante));
    }
}
