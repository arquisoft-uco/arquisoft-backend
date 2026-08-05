package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilFinder;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemFichaPerfilUseCaseTest {

    @Mock
    private ItemFichaPerfilOutputPort itemOutputPort;

    @Mock
    private ItemFichaPerfilFinder itemFichaPerfilFinder;

    @Mock
    private RevisionItemQueryOutputPort revisionQueryPort;

    @Mock
    private RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private RemoverItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain item = itemReconstruido(itemId, fichaPerfilId);

        when(itemFichaPerfilFinder.obtener(itemId)).thenReturn(item);
        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(0L);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemFichaPerfilFinder, times(1)).obtener(itemId);
        verify(removerItemFichaPerfilValidator, times(1)).validar(any(), any());
        verify(revisionQueryPort, times(1)).contarPorItem(itemId);
        verify(itemOutputPort, times(1)).eliminarPorId(itemId);
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new ItemFichaPerfilNoEncontradoException(itemId)).when(itemFichaPerfilFinder).obtener(itemId);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception)
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(itemId.toString());

        ItemFichaPerfilNoEncontradoException notFoundException =
                (ItemFichaPerfilNoEncontradoException) exception;
        assertThat(notFoundException.getErrorCode())
                .isEqualTo(FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO);

        verify(removerItemFichaPerfilValidator, never()).validar(any(), any());
        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain item = itemReconstruido(itemId, fichaPerfilId);

        when(itemFichaPerfilFinder.obtener(itemId)).thenReturn(item);
        doThrow(new FichaNoPropietarioException(fichaPerfilId, estudianteId))
                .when(removerItemFichaPerfilValidator).validar(any(), any());

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(FichaNoPropietarioException.class);

        FichaNoPropietarioException authException = (FichaNoPropietarioException) exception;
        assertThat(authException.getErrorCode())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO);

        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain item = itemReconstruido(itemId, fichaPerfilId);

        when(itemFichaPerfilFinder.obtener(itemId)).thenReturn(item);
        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(2L);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(DomainValidationException.class);

        DomainValidationException validationException = (DomainValidationException) exception;
        assertThat(validationException.getValidationResult().getErrores()).hasSize(1);
        assertThat(validationException.getValidationResult().getErrores().get(0).campo())
                .isEqualTo(FichasFields.ItemFichaPerfil.REVISIONES);
        assertThat(validationException.getValidationResult().getErrores().get(0).codigoError())
                .isEqualTo(FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES);

        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    private ItemFichaPerfilDomain itemReconstruido(UUID itemId, UUID fichaPerfilId) {
        return ItemFichaPerfilDomain.reconstruir(
                itemId,
                fichaPerfilId,
                TipoItem.OBJETIVO_GENERAL,
                "Contenido del ítem"
        );
    }
}
