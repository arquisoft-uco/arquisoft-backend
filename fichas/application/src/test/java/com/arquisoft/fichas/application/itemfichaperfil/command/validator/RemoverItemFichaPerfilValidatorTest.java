package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.RemoverItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.model.FichaPerfilDelItem;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
        validator.validar(item, estudiante, Optional.of(fichaPerfil), true, 0L);

        // Assert
        InOrder inOrder = inOrder(itemFichaPerfilExisteRule, estudiantePropietarioFichaRule,
                itemSinRevisionesRule);
        inOrder.verify(itemFichaPerfilExisteRule)
                .validar(new FichaPerfilDelItem(item, Optional.of(fichaPerfil)));
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
        validator.validar(item, estudiante, Optional.of(fichaPerfil), true, 3L);

        // Assert
        verify(itemSinRevisionesRule).validar(new RevisionesItem(item, 3L));
    }

    @Test
    void noDebeAplicarLaReglaDePropiedad_cuandoElItemNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validar(item, estudiante, Optional.empty(), false, 0L);

        // Assert
        verify(itemFichaPerfilExisteRule).validar(new FichaPerfilDelItem(item, Optional.empty()));
        verify(estudiantePropietarioFichaRule, never()).validar(ArgumentMatchers.any());
    }
}
