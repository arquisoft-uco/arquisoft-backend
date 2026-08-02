package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesFichaPerfilUseCaseTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private AsignarEstudiantesFichaPerfilValidator asignarEstudiantesFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstudiantesFichaPerfilUseCaseImpl useCase;

    @Test
    void debeAsignarEstudiantes_cuandoLasReglasPasan() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId, List.of(UUID.randomUUID(), UUID.randomUUID()));

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(2)).guardar(any());
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        List<UUID> estudiantes = List.of(UUID.randomUUID());
        var command = new AsignarEstudiantesFichaPerfilCommand(fichaPerfilId, estudiantes);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(asignarEstudiantesFichaPerfilValidator)
                .validar(eq(fichaPerfilId), eq(estudiantes), anyList());
    }

    @Test
    void debePropagarFichaNoEncontrada_cuandoElValidadorFalla() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(fichaPerfilId, List.of(UUID.randomUUID()));

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfilId.toString());
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarEstudianteNoEncontrado_cuandoElValidadorFalla() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(UUID.randomUUID(), List.of(estudiante));

        doThrow(new EstudianteNoEncontradoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(estudiante.toString());
        assertThat(((EstudianteNoEncontradoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarEstudianteDuplicado_cuandoElValidadorFalla() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(UUID.randomUUID(), List.of(estudiante));

        doThrow(new EstudianteDuplicadoException(estudiante))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudiante.toString());
        assertThat(((EstudianteDuplicadoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarLimiteExcedido_cuandoElValidadorFalla() {
        // Arrange
        var command = new AsignarEstudiantesFichaPerfilCommand(
                UUID.randomUUID(), List.of(UUID.randomUUID(), UUID.randomUUID()));

        doThrow(new CupoEstudiantesExcedidoException(FichasMessages.FichaPerfil.ESTUDIANTES_MAX))
                .when(asignarEstudiantesFichaPerfilValidator).validar(any(), anyList(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(CupoEstudiantesExcedidoException.class)
                .hasMessage(FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                        FichasMessages.FichaPerfil.ESTUDIANTES_MAX));
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }
}
