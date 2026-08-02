package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ItemFichaPerfilValidator;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
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

@ExtendWith(MockitoExtension.class)
class AgregarItemFichaPerfilUseCaseTest {

    private static final String TIPO_ITEM = "OBJETIVO_GENERAL";
    private static final String CONTENIDO = "Contenido válido";

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private FichaPerfilValidator fichaPerfilValidator;

    @Mock
    private ItemFichaPerfilValidator itemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeAgregarItem_cuandoDatosValidos() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(itemFichaPerfilOutputPort, times(1)).guardar(any(ItemFichaPerfilAggregate.class));
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoFichaNoExiste() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();
        doThrow(new FichaPerfilNoEncontradaException(command.fichaPerfil()))
                .when(fichaPerfilValidator).validarFichaExiste(command.fichaPerfil());

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(FichaPerfilNoEncontradaException.class);
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();
        doThrow(new ItemFichaNoPropiaException(command.fichaPerfil()))
                .when(itemFichaPerfilValidator)
                .validarFichaPropia(command.fichaPerfil(), command.estudiante());

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception)
                .isInstanceOf(ItemFichaNoPropiaException.class)
                .hasMessageContaining(command.fichaPerfil().toString());
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeLanzarItemTipoDuplicado_cuandoTipoYaExisteEnFicha() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();
        doThrow(new ItemTipoDuplicadoException(TIPO_ITEM))
                .when(itemFichaPerfilValidator)
                .validarTipoNoDuplicado(command.fichaPerfil(), TIPO_ITEM);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(ItemTipoDuplicadoException.class);
        assertThat(((ApplicationException) exception).getErrorCode())
                .isEqualTo(FichasMessages.ItemFichaPerfil.ITEM_TIPO_DUPLICADO);
        assertThat(exception.getMessage())
                .isEqualTo(FichasMessages.ItemFichaPerfil.TIPO_ITEM_DUPLICADO_MSG.formatted(TIPO_ITEM));
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeValidarEnOrdenCorrecto() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();

        // Act
        useCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilValidator, itemFichaPerfilValidator, itemFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilValidator).validarFichaExiste(command.fichaPerfil());
        inOrder.verify(itemFichaPerfilValidator)
                .validarFichaPropia(command.fichaPerfil(), command.estudiante());
        inOrder.verify(itemFichaPerfilValidator)
                .validarTipoNoDuplicado(command.fichaPerfil(), TIPO_ITEM);
        inOrder.verify(itemFichaPerfilOutputPort).guardar(any(ItemFichaPerfilAggregate.class));
    }

    @Test
    void debeConstruirAgregadoAntesDeConsultarLaBaseDeDatos_cuandoTipoItemEsInvalido() {
        // Arrange
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                UUID.randomUUID(), "TIPO_INEXISTENTE", CONTENIDO, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(fichaPerfilValidator, never()).validarFichaExiste(any());
        verify(itemFichaPerfilValidator, never()).validarFichaPropia(any(), any());
        verify(itemFichaPerfilValidator, never()).validarTipoNoDuplicado(any(), any());
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarUUID_cuandoExitoso() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();

        // Act
        UUID resultado = useCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
    }

    @Test
    void debePropagar_cuandoRepositorioFalla() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();
        doThrow(new RuntimeException("DB error")).when(itemFichaPerfilOutputPort).guardar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    private AgregarItemFichaPerfilCommand comando() {
        return new AgregarItemFichaPerfilCommand(
                UUID.randomUUID(),
                TIPO_ITEM,
                CONTENIDO,
                UUID.randomUUID()
        );
    }
}
