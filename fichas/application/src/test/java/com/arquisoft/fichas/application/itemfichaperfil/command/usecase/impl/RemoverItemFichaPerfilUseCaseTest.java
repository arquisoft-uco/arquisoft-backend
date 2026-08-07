package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.mapper.RemoverItemFichaPerfilMapper;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
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
    private RevisionItemQueryOutputPort revisionQueryPort;

    @Mock
    private RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;

    @Mock
    private ItemSinRevisionesRule itemSinRevisionesRule;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private RemoverItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(0L);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        useCase.ejecutar(RemoverItemFichaPerfilMapper.toDomain(command));

        // Assert
        verify(removerItemFichaPerfilValidator, times(1)).validar(itemId, estudianteId);
        verify(revisionQueryPort, times(1)).contarPorItem(itemId);
        verify(itemOutputPort, times(1)).removerItem(itemId);
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new ItemFichaPerfilNoEncontradoException(itemId))
                .when(removerItemFichaPerfilValidator).validar(any(), any());

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(RemoverItemFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(exception)
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(itemId.toString());

        ItemFichaPerfilNoEncontradoException notFoundException =
                (ItemFichaPerfilNoEncontradoException) exception;
        assertThat(notFoundException.getCodigoError())
                .isEqualTo(FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO);

        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        doThrow(new FichaNoPropietarioException(fichaPerfilId, estudianteId))
                .when(removerItemFichaPerfilValidator).validar(any(), any());

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(RemoverItemFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(exception).isInstanceOf(FichaNoPropietarioException.class);

        FichaNoPropietarioException authException = (FichaNoPropietarioException) exception;
        assertThat(authException.getCodigoError())
                .isEqualTo(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO);

        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(2L);
        doThrow(new ItemConRevisionesException(itemId))
                .when(itemSinRevisionesRule).validar(any());

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(RemoverItemFichaPerfilMapper.toDomain(command)));

        // Assert
        assertThat(exception)
                .isInstanceOf(ItemConRevisionesException.class)
                .hasMessageContaining(itemId.toString());

        verify(itemOutputPort, never()).removerItem(any());
    }
}
