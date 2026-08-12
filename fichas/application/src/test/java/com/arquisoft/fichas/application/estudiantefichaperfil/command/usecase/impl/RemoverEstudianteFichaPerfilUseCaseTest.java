package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
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
class RemoverEstudianteFichaPerfilUseCaseTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private EstudiantesExistentesFinder estudiantesExistentesFinder;

    @Mock
    private VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;

    @Mock
    private RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private RemoverEstudianteFichaPerfilUseCaseImpl removerEstudianteFichaPerfilUseCase;

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();

    @Test
    void debeDesvincularAlEstudiante_cuandoDatosValidos() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);
        stubConsultas(true, List.of(estudiante), true);

        // Act
        removerEstudianteFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(1)).desvincularEstudiante(fichaPerfil, estudiante);
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);
        stubConsultas(true, List.of(estudiante), true);

        // Act
        removerEstudianteFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, estudiantesExistentesFinder,
                vinculoEstudianteFichaExisteFinder, removerEstudianteFichaPerfilValidator,
                estudianteFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(fichaPerfil);
        inOrder.verify(estudiantesExistentesFinder).obtener(List.of(estudiante));
        inOrder.verify(vinculoEstudianteFichaExisteFinder)
                .obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante));
        inOrder.verify(removerEstudianteFichaPerfilValidator)
                .validar(entrada, true, List.of(estudiante), true);
        inOrder.verify(estudianteFichaPerfilOutputPort).desvincularEstudiante(fichaPerfil, estudiante);
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);
        stubConsultas(false, List.of(estudiante), false);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfil))
                .when(removerEstudianteFichaPerfilValidator)
                .validar(entrada, false, List.of(estudiante), false);

        // Act & Assert
        assertThatThrownBy(() -> removerEstudianteFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(estudianteFichaPerfilOutputPort, never()).desvincularEstudiante(any(), any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoNoExisteElVinculo() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);
        stubConsultas(true, List.of(estudiante), false);
        doThrow(new EstudianteFichaPerfilNoEncontradoException(estudiante, fichaPerfil))
                .when(removerEstudianteFichaPerfilValidator)
                .validar(entrada, true, List.of(estudiante), false);

        // Act & Assert
        assertThatThrownBy(() -> removerEstudianteFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).desvincularEstudiante(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = RemocionEstudianteFichaPerfilDomain.crear(fichaPerfil, estudiante);
        stubConsultas(true, List.of(estudiante), true);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estudianteFichaPerfilOutputPort).desvincularEstudiante(fichaPerfil, estudiante);

        // Act & Assert
        assertThatThrownBy(() -> removerEstudianteFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultas(boolean fichaExiste, List<UUID> estudiantesExistentes, boolean vinculoExiste) {
        when(fichaPerfilExisteFinder.obtener(fichaPerfil)).thenReturn(fichaExiste);
        when(estudiantesExistentesFinder.obtener(List.of(estudiante))).thenReturn(estudiantesExistentes);
        when(vinculoEstudianteFichaExisteFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante)))
                .thenReturn(vinculoExiste);
    }
}
