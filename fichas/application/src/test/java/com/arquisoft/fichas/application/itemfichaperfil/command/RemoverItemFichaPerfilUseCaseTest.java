package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.revisionitem.query.port.out.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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
    private FichaPerfilValidator fichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemFichaPerfilUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoDatosValidos() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);

        when(itemOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(0L);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        useCase.ejecutar(command);

        // Assert
        verify(itemOutputPort, times(1)).buscarPorId(itemId);
        verify(fichaPerfilValidator, times(1)).validarEstudiantePropietario(
                new PropietarioFichaCriteria(fichaPerfilId, estudianteId));
        verify(revisionQueryPort, times(1)).contarPorItem(itemId);
        verify(itemOutputPort, times(1)).eliminarPorId(itemId);
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        when(itemOutputPort.buscarPorId(itemId)).thenReturn(Optional.empty());

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
                .isEqualTo(FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO);

        verify(fichaPerfilValidator, never()).validarEstudiantePropietario(any());
        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);

        when(itemOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        doThrow(new FichaNoPropietarioException(fichaPerfilId, estudianteId))
                .when(fichaPerfilValidator).validarEstudiantePropietario(
                        new PropietarioFichaCriteria(fichaPerfilId, estudianteId));

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(FichaNoPropietarioException.class);

        FichaNoPropietarioException authException = (FichaNoPropietarioException) exception;
        assertThat(authException.getErrorCode())
                .isEqualTo(FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO);

        verify(revisionQueryPort, never()).contarPorItem(any());
        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = itemReconstruido(itemId, fichaPerfilId);

        when(itemOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(revisionQueryPort.contarPorItem(itemId)).thenReturn(2L);

        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);

        // Act
        Throwable exception = catchThrowable(() -> useCase.ejecutar(command));

        // Assert
        assertThat(exception).isInstanceOf(DomainValidationException.class);

        DomainValidationException validationException = (DomainValidationException) exception;
        assertThat(validationException.getValidationResult().getErrores()).hasSize(1);
        assertThat(validationException.getValidationResult().getErrores().get(0).campo())
                .isEqualTo(FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES);
        assertThat(validationException.getValidationResult().getErrores().get(0).codigoError())
                .isEqualTo(FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES);

        verify(itemOutputPort, never()).eliminarPorId(any());
    }

    private ItemFichaPerfilAggregate itemReconstruido(UUID itemId, UUID fichaPerfilId) {
        return ItemFichaPerfilAggregate.reconstruir(
                itemId,
                fichaPerfilId,
                TipoItem.OBJETIVO_GENERAL,
                "Contenido del ítem"
        );
    }
}
