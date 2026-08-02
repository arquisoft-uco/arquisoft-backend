package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ItemFichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemNoEncontradoException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilUseCaseTest {

    private static final String CONTENIDO_NUEVO = "Contenido modificado";

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Mock
    private ItemFichaPerfilValidator itemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeModificar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, estudianteId);

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).buscarPorId(itemId);
        verify(itemFichaPerfilValidator, times(1)).validarFichaPropia(fichaPerfilId, estudianteId);
        verify(itemFichaPerfilOutputPort, times(1)).guardar(item);
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(ItemNoEncontradoException.class);

        verify(itemFichaPerfilValidator, never()).validarFichaPropia(any(), any());
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, estudianteId);

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        doThrow(new ItemFichaNoPropiaException(fichaPerfilId))
                .when(itemFichaPerfilValidator).validarFichaPropia(fichaPerfilId, estudianteId);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidation_cuandoContenidoInvalido() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, "", UUID.randomUUID());

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarDomainValidation_cuandoFichaEnEstadoTerminal() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.APROBADA));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarFichaPerfilNoEncontrada_cuandoFichaSinEstadoRegistrado() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);
        var command = new ModificarItemFichaPerfilCommand(itemId, CONTENIDO_NUEVO, UUID.randomUUID());

        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));
        doThrow(new RuntimeException("Error de BD")).when(itemFichaPerfilOutputPort).guardar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error de BD");

        verify(itemFichaPerfilOutputPort, times(1)).guardar(item);
    }

    private ItemFichaPerfilAggregate itemReconstruido(UUID itemId, UUID fichaPerfilId) {
        return ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );
    }
}
