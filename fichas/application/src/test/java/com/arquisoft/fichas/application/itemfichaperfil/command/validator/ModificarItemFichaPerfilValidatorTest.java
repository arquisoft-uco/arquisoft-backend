package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.ModificarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.model.FichaPerfilDelItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ModificarItemFichaPerfilValidatorTest {

    @Mock
    private ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;

    @Mock
    private ItemFichaPropiaRule itemFichaPropiaRule;

    @Mock
    private EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @InjectMocks
    private ModificarItemFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoElItemExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID fichaPerfil = UUID.randomUUID();
        var estadoActual = Optional.of(EstadoFicha.EN_CONSTRUCCION);

        // Act
        validator.validar(item, estudiante, Optional.of(fichaPerfil), true, estadoActual);

        // Assert
        InOrder inOrder = inOrder(itemFichaPerfilExisteRule, itemFichaPropiaRule,
                estadoFichaPerfilEnTerminalRule);
        inOrder.verify(itemFichaPerfilExisteRule)
                .validar(new FichaPerfilDelItem(item, Optional.of(fichaPerfil)));
        inOrder.verify(itemFichaPropiaRule).validar(new PropiedadFicha(fichaPerfil, estudiante, true));
        inOrder.verify(estadoFichaPerfilEnTerminalRule)
                .validar(new EstadoActualFicha(fichaPerfil, estadoActual));
    }

    @Test
    void noDebeAplicarLasReglasDependientesDeLaFicha_cuandoElItemNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        validator.validar(item, estudiante, Optional.empty(), false, Optional.empty());

        // Assert — sin ficha no hay nada que comprobar sobre propiedad ni estado
        verify(itemFichaPerfilExisteRule).validar(new FichaPerfilDelItem(item, Optional.empty()));
        verify(itemFichaPropiaRule, never()).validar(org.mockito.ArgumentMatchers.any());
        verify(estadoFichaPerfilEnTerminalRule, never()).validar(org.mockito.ArgumentMatchers.any());
    }
}
