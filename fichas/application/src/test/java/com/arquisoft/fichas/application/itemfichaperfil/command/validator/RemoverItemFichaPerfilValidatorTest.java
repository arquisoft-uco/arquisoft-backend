package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.RemoverItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverItemFichaPerfilValidatorTest {

    @Mock
    private ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;

    @Mock
    private EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @Mock
    private ItemSinRevisionesRule itemSinRevisionesRule;

    @InjectMocks
    private RemoverItemFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoElItemExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID fichaPerfil = UUID.randomUUID();

        // Act
        validator.validar(item, estudiante, fichaPerfil, true, true, 0L);

        // Assert
        InOrder inOrder = inOrder(itemFichaPerfilExisteRule, estudiantePropietarioFichaRule,
                itemSinRevisionesRule);
        inOrder.verify(itemFichaPerfilExisteRule)
                .validar(new ExistenciaItemFichaPerfil(item, true));
        inOrder.verify(estudiantePropietarioFichaRule)
                .validar(new PropiedadFicha(fichaPerfil, estudiante, true));
        inOrder.verify(itemSinRevisionesRule).validar(new RevisionesItem(item, 0L));
    }

    @Test
    void debeTrasladarElConteoDeRevisiones_cuandoElItemTieneRevisiones() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID fichaPerfil = UUID.randomUUID();

        // Act
        validator.validar(item, estudiante, fichaPerfil, true, true, 3L);

        // Assert
        verify(itemSinRevisionesRule).validar(new RevisionesItem(item, 3L));
    }

    @Test
    void noDebeAplicarLaReglaDePropiedad_cuandoElItemNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(itemFichaPerfilExisteRule).validar(new ExistenciaItemFichaPerfil(item, false));

        // Act
        assertThatThrownBy(() -> validator.validar(
                item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false, 0L))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        // Assert
        verify(estudiantePropietarioFichaRule, never()).validar(ArgumentMatchers.any());
        verify(itemSinRevisionesRule, never()).validar(ArgumentMatchers.any());
    }
}
