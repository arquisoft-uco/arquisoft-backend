package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    private AgregarItemFichaPerfilValidator agregarItemFichaPerfilValidator;


    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

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
        verify(itemFichaPerfilOutputPort, times(1)).guardar(any(ItemFichaPerfilDomain.class));
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoFichaNoExiste() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();
        doThrow(new FichaPerfilNoEncontradaException(command.fichaPerfil()))
                .when(agregarItemFichaPerfilValidator).validar(any(), any());

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
                .when(agregarItemFichaPerfilValidator)
                .validar(any(), any());

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
                .when(agregarItemFichaPerfilValidator)
                .validar(any(), any());

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(ItemTipoDuplicadoException.class);
        assertThat(((ApplicationException) exception).getErrorCode())
                .isEqualTo(FichasCodes.ItemFichaPerfil.ITEM_TIPO_DUPLICADO);
        assertThat(exception.getMessage())
                .isEqualTo(Messages.formatear(FichasKeys.ItemFichaPerfil.ERROR_TIPO_DUPLICADO, TIPO_ITEM));
        verify(itemFichaPerfilOutputPort, never()).guardar(any());
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        AgregarItemFichaPerfilCommand command = comando();

        // Act
        useCase.ejecutar(command);

        // Assert
        InOrder inOrder = inOrder(agregarItemFichaPerfilValidator, itemFichaPerfilOutputPort);
        inOrder.verify(agregarItemFichaPerfilValidator).validar(any(), any());
        inOrder.verify(itemFichaPerfilOutputPort).guardar(any(ItemFichaPerfilDomain.class));
    }

    @Test
    void debeConstruirAgregadoAntesDeConsultarLaBaseDeDatos_cuandoTipoItemEsInvalido() {
        // Arrange
        AgregarItemFichaPerfilCommand command = new AgregarItemFichaPerfilCommand(
                UUID.randomUUID(), "TIPO_INEXISTENTE", CONTENIDO, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(command))
                .isInstanceOf(DomainValidationException.class);

        verify(agregarItemFichaPerfilValidator, never()).validar(any(), any());
        verify(agregarItemFichaPerfilValidator, never()).validar(any(), any());
        verify(agregarItemFichaPerfilValidator, never()).validar(any(), any());
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
