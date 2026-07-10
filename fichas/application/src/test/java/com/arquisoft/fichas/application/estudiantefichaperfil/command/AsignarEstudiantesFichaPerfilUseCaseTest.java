package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private EstudianteQueryOutputPort estudianteQueryOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private AsignarEstudiantesFichaPerfilUseCase useCase;

    @Test
    void debeAsignarEstudiantes_cuandoListaValidaYLimiteNoExcedido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        List<UUID> estudiantesIds = List.of(estudiante1, estudiante2);
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                estudiantesIds
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante2)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudiante1)).thenReturn(false);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudiante2)).thenReturn(false);
        when(estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId)).thenReturn(1L);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(estudianteFichaPerfilOutputPort, times(2)).guardar(any());
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoFichaIdNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(estudiante1)
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(false);

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfilId.toString());
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_cuandoUUIDNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(estudiante1, estudiante2)
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante2)).thenReturn(false);

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(estudiante2.toString());
        assertThat(((EstudianteNoEncontradoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteDuplicado_cuandoUUIDRepetidoEnLista() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(estudiante1, estudiante1)
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudiante1.toString());
        assertThat(((EstudianteDuplicadoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteDuplicado_cuandoYaAsignadoEnBD() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(estudiante1, estudiante2)
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudiante1)).thenReturn(true);

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudiante1.toString());
        assertThat(((EstudianteDuplicadoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidationException_cuandoExistentes2MasNuevos2() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        AsignarEstudiantesFichaPerfilCommand command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(estudiante1, estudiante2)
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante1)).thenReturn(true);
        when(estudianteQueryOutputPort.existsById(estudiante2)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudiante1)).thenReturn(false);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudiante2)).thenReturn(false);
        when(estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId)).thenReturn(2L);

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                        FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                ));

        DomainValidationException domainEx = (DomainValidationException) ex;
        assertThat(domainEx.getValidationResult().getErrors())
                .anyMatch(error -> error.errorCode().equals(
                        FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO
                ));
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }
}
