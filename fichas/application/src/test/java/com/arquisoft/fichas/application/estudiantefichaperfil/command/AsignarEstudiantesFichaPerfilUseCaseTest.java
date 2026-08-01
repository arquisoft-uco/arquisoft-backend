package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.EstudiantesFichaValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private FichaPerfilValidator fichaPerfilValidator;

    @Mock
    private EstudiantesFichaValidator estudiantesFichaValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstudiantesFichaPerfilUseCaseImpl useCase;

    @Test
    void debeAsignarEstudiantes_cuandoListaValidaYLimiteNoExcedido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId,
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );

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
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId, List.of(UUID.randomUUID()));

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(fichaPerfilValidator).validarFichaExiste(fichaPerfilId);

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
        UUID estudianteInexistente = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                UUID.randomUUID(), List.of(UUID.randomUUID(), estudianteInexistente));

        doThrow(new EstudianteNoEncontradoException(estudianteInexistente))
                .when(estudiantesFichaValidator).validarExistencia(anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(estudianteInexistente.toString());
        assertThat(((EstudianteNoEncontradoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeValidarDuplicadosAntesDeConsultarLaBaseDeDatos_cuandoUUIDRepetidoEnLista() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                UUID.randomUUID(), List.of(estudiante, estudiante));

        doThrow(new EstudianteDuplicadoException(estudiante))
                .when(estudiantesFichaValidator).validarSinDuplicados(anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudiante.toString());
        verify(fichaPerfilValidator, never()).validarFichaExiste(any());
        verify(estudiantesFichaValidator, never()).validarExistencia(anyList());
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarEstudianteDuplicado_cuandoYaAsignadoEnBD() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteYaAsignado = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId, List.of(estudianteYaAsignado, UUID.randomUUID()));

        doThrow(new EstudianteDuplicadoException(estudianteYaAsignado))
                .when(estudiantesFichaValidator).validarNoVinculados(any(), anyList());

        // Act
        Throwable ex = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(ex)
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(estudianteYaAsignado.toString());
        assertThat(((EstudianteDuplicadoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO);
        verify(estudianteFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidationException_cuandoExistentes2MasNuevos2() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId, List.of(UUID.randomUUID(), UUID.randomUUID()));

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

    @Test
    void debeValidarIntegridadAntesQueExistencia() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var command = new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfilId, List.of(UUID.randomUUID()));

        when(estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId)).thenReturn(0L);

        // Act
        useCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(estudiantesFichaValidator, fichaPerfilValidator);
        inOrder.verify(estudiantesFichaValidator).validarSinDuplicados(anyList());
        inOrder.verify(fichaPerfilValidator).validarFichaExiste(fichaPerfilId);
        inOrder.verify(estudiantesFichaValidator).validarExistencia(anyList());
        inOrder.verify(estudiantesFichaValidator).validarNoVinculados(any(), anyList());
    }
}
