package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.mapper.ModificarItemFichaPerfilMapper;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilUseCaseTest {

    private static final String CONTENIDO_NUEVO = "Contenido modificado";

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

    @InjectMocks
    private ModificarItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeModificar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, estudianteId);

        // Act
        useCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command));

        // Assert
        verify(modificarItemFichaPerfilValidator, times(1)).validar(itemId, estudianteId);
        verify(itemFichaPerfilOutputPort, times(1)).actualizarContenido(itemId, CONTENIDO_NUEVO);
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        doThrow(new ItemFichaPerfilNoEncontradoException(itemId))
                .when(modificarItemFichaPerfilValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), any());
    }

    @Test
    void debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, estudianteId);

        doThrow(new ItemFichaNoPropiaException(fichaPerfilId))
                .when(modificarItemFichaPerfilValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), any());
    }

    @Test
    void debeRechazarEnElMapeo_cuandoContenidoInvalido() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, "", UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemFichaPerfilMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class);

        verifyNoInteractions(itemFichaPerfilOutputPort, modificarItemFichaPerfilValidator);
    }

    @Test
    void debePropagarEstadoFichaPerfilTerminalException_cuandoFichaEnEstadoTerminal() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        doThrow(new EstadoFichaPerfilTerminalException(EstadoFicha.APROBADA))
                .when(modificarItemFichaPerfilValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), any());
    }

    @Test
    void debePropagarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        doThrow(new RuntimeException("Error de BD"))
                .when(itemFichaPerfilOutputPort).actualizarContenido(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error de BD");

        verify(itemFichaPerfilOutputPort, times(1)).actualizarContenido(itemId, CONTENIDO_NUEVO);
    }
}
