package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverEstudianteFichaPerfilUseCaseTest {

    @Mock
    private EstudianteQueryOutputPort estudianteQueryOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private FichaPerfilValidator fichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverEstudianteFichaPerfilUseCase useCase;

    @Test
    void debeRemover_cuandoRelacionExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        when(estudianteQueryOutputPort.existePorId(estudianteId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(fichaPerfilValidator, times(1)).validarFichaExiste(fichaPerfilId);
        verify(estudianteQueryOutputPort, times(1)).existePorId(estudianteId);
        verify(estudianteFichaPerfilOutputPort, times(1))
                .existePorFichaYEstudiante(fichaPerfilId, estudianteId);
        verify(estudianteFichaPerfilOutputPort, times(1)).eliminar(fichaPerfilId, estudianteId);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(fichaPerfilValidator).validarFichaExiste(fichaPerfilId);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessage(FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG.formatted(fichaPerfilId));

        verify(estudianteQueryOutputPort, never()).existePorId(any());
        verify(estudianteFichaPerfilOutputPort, never()).existePorFichaYEstudiante(any(), any());
        verify(estudianteFichaPerfilOutputPort, never()).eliminar(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        when(estudianteQueryOutputPort.existePorId(estudianteId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessage(FichasMessages.Estudiante.NO_ENCONTRADO_MSG.formatted(estudianteId));

        verify(fichaPerfilValidator, times(1)).validarFichaExiste(fichaPerfilId);
        verify(estudianteFichaPerfilOutputPort, never()).existePorFichaYEstudiante(any(), any());
        verify(estudianteFichaPerfilOutputPort, never()).eliminar(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRelacionNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        when(estudianteQueryOutputPort.existePorId(estudianteId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class)
                .hasMessage(FichasMessages.EstudianteFichaPerfil.RELACION_NO_ENCONTRADA_MSG
                        .formatted(estudianteId, fichaPerfilId));

        verify(estudianteFichaPerfilOutputPort, never()).eliminar(any(), any());
    }

    @Test
    void debeInvocarEliminar_cuandoValidacionesPasan() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        when(estudianteQueryOutputPort.existePorId(estudianteId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(1)).eliminar(fichaPerfilId, estudianteId);
    }
}
