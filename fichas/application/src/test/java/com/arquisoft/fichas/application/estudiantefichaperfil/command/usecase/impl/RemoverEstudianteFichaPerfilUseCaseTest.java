package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper.RemoverEstudianteFichaPerfilMapper;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Las reglas de negocio se prueban en fichas:domain; aqui solo se comprueba que el
 * caso de uso valida antes de eliminar y que propaga lo que lance el validador.
 */
@ExtendWith(MockitoExtension.class)
class RemoverEstudianteFichaPerfilUseCaseTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private RemoverEstudianteFichaPerfilUseCaseImpl useCase;

    @Test
    void debeRemover_cuandoValidacionesPasan() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        // Act
        useCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command));

        // Assert
        verify(removerEstudianteFichaPerfilValidator, times(1)).validar(any());
        verify(estudianteFichaPerfilOutputPort, times(1)).desvincularEstudiante(fichaPerfilId, estudianteId);
    }

    @Test
    void debeValidarAntesDeEliminar_cuandoSeEjecuta() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        // Act
        useCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command));

        // Assert
        InOrder inOrder = inOrder(removerEstudianteFichaPerfilValidator, estudianteFichaPerfilOutputPort);
        inOrder.verify(removerEstudianteFichaPerfilValidator).validar(any());
        inOrder.verify(estudianteFichaPerfilOutputPort).desvincularEstudiante(fichaPerfilId, estudianteId);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(removerEstudianteFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfilId.toString());

        verify(estudianteFichaPerfilOutputPort, never()).desvincularEstudiante(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        doThrow(new EstudianteNoEncontradoException(estudianteId))
                .when(removerEstudianteFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(estudianteId.toString());

        verify(estudianteFichaPerfilOutputPort, never()).desvincularEstudiante(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRelacionNoExiste() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);

        doThrow(new EstudianteFichaPerfilNoEncontradoException(estudianteId, fichaPerfilId))
                .when(removerEstudianteFichaPerfilValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);

        verify(estudianteFichaPerfilOutputPort, never()).desvincularEstudiante(any(), any());
    }
}
