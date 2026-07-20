package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemNoEncontradoException;
import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilUseCaseTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @InjectMocks
    private ModificarItemFichaPerfilUseCase useCase;

    @Test
    void debeModificar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nuevoContenido = "Contenido modificado";

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                nuevoContenido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).existsById(itemId);
        verify(itemFichaPerfilOutputPort, times(1)).buscarPorId(itemId);
        verify(fichaPerfilQueryOutputPort, times(1)).esEstudiantePropietario(fichaPerfilId, estudianteId);
        verify(itemFichaPerfilOutputPort, times(1)).guardar(item);
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nuevoContenido = "Contenido modificado";

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                nuevoContenido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(false);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(ItemNoEncontradoException.class);

        verify(itemFichaPerfilOutputPort, times(1)).existsById(itemId);
        verify(itemFichaPerfilOutputPort, never()).buscarPorId(any());
        verify(fichaPerfilQueryOutputPort, never()).esEstudiantePropietario(any(), any());
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nuevoContenido = "Contenido modificado";

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                nuevoContenido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(false);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, times(1)).existsById(itemId);
        verify(itemFichaPerfilOutputPort, times(1)).buscarPorId(itemId);
        verify(fichaPerfilQueryOutputPort, times(1)).esEstudiantePropietario(fichaPerfilId, estudianteId);
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarDomainValidation_cuandoContenidoInvalido() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String contenidoInvalido = "";

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                contenidoInvalido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(DomainValidationException.class);

        verify(itemFichaPerfilOutputPort, times(1)).existsById(itemId);
        verify(itemFichaPerfilOutputPort, times(1)).buscarPorId(itemId);
        verify(fichaPerfilQueryOutputPort, times(1)).esEstudiantePropietario(fichaPerfilId, estudianteId);
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debePropagarDomainValidation_cuandoFichaEnEstadoTerminal() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                "Contenido modificado",
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.APROBADA));

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(DomainValidationException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarFichaPerfilNoEncontrada_cuandoFichaSinEstadoRegistrado() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                "Contenido modificado",
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId)).thenReturn(Optional.empty());

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLlamarGuardar_cuandoModificacionExitosa() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nuevoContenido = "Contenido modificado";

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                nuevoContenido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).guardar(item);
    }

    @Test
    void debePropagarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String nuevoContenido = "Contenido modificado";

        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                com.arquisoft.fichas.domain.tipoitem.TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        ModificarItemFichaPerfilCommand command = new ModificarItemFichaPerfilCommand(
                itemId,
                nuevoContenido,
                estudianteId
        );

        when(itemFichaPerfilOutputPort.existsById(itemId)).thenReturn(true);
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(fichaPerfilId, estudianteId)).thenReturn(true);
        when(estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId))
                .thenReturn(Optional.of(EstadoFicha.EN_CONSTRUCCION));
        doThrow(new RuntimeException("Error de BD")).when(itemFichaPerfilOutputPort).guardar(any());

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThatThrownBy(() -> {
            throw exception;
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Error de BD");

        verify(itemFichaPerfilOutputPort, times(1)).guardar(item);
    }
}
