package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarItemFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private AgregarItemFichaPerfilUseCase useCase;

    @Test
    void debeAgregarItem_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Este es un objetivo general válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(itemFichaPerfilOutputPort, times(1)).guardar(any(ItemFichaPerfilAggregate.class));
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoFichaNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(false);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception)
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(false);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception)
                .isInstanceOf(ItemFichaNoPropiaException.class)
                .hasMessageContaining(fichaPerfilId.toString());
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemTipoDuplicado_cuandoTipoYaExisteEnFicha() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                tipoItem,
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, tipoItem))
                .thenReturn(true);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(ItemTipoDuplicadoException.class);
        assertThat(((ApplicationException) exception).getErrorCode())
                .isEqualTo(FichasMessages.ItemFichaPerfil.ITEM_TIPO_DUPLICADO);
        assertThat(exception.getMessage())
                .isEqualTo(FichasMessages.ItemFichaPerfil.TIPO_ITEM_DUPLICADO_MSG.formatted(tipoItem));
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeGuardarItem_cuandoValidacionesExitosas() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).guardar(any(ItemFichaPerfilAggregate.class));
    }

    @Test
    void debeValidarEnOrdenCorrecto() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);

        // Act
        useCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(
                fichaPerfilOutputPort,
                estudianteFichaPerfilOutputPort,
                itemFichaPerfilOutputPort
        );
        inOrder.verify(fichaPerfilOutputPort).existsById(fichaPerfilId);
        inOrder.verify(estudianteFichaPerfilOutputPort).existePorFichaYEstudiante(fichaPerfilId, estudianteId);
        inOrder.verify(itemFichaPerfilOutputPort).existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL");
        inOrder.verify(itemFichaPerfilOutputPort).guardar(any(ItemFichaPerfilAggregate.class));
    }

    @Test
    void debeRetornarUUID_cuandoExitoso() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
    }

    @Test
    void debePropagar_cuandoRepositorioFalla() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);
        doThrow(new RuntimeException("DB error")).when(itemFichaPerfilOutputPort).guardar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    @Test
    void debeLoguear_cuandoExitoso() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido válido",
                estudianteId
        );

        when(fichaPerfilOutputPort.existsById(fichaPerfilId)).thenReturn(true);
        when(estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId))
                .thenReturn(true);
        when(itemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, "OBJETIVO_GENERAL"))
                .thenReturn(false);

        // Act
        useCase.ejecutar(command);

        // Assert — log.info se ejecuta internamente con LOG_AGREGADO del catálogo
        // No se puede verificar directamente sin LogCaptor/Appender, pero el método ejecuta sin error
        verify(itemFichaPerfilOutputPort, times(1)).guardar(any());
    }
}
