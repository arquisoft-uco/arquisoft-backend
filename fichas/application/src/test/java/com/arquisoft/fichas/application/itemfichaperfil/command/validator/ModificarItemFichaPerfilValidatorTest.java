package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.ModificarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ModificarItemFichaPerfilValidatorTest {

    @Mock
    private ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;

    @Mock
    private ItemFichaPropiaRule itemFichaPropiaRule;

    @Mock
    private EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;

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
        var estadoActual = EstadoFichaPerfilDomain.crear(fichaPerfil);

        // Act
        validator.validar(item, estudiante, fichaPerfil, true, true, estadoActual);

        // Assert
        InOrder inOrder = inOrder(itemFichaPerfilExisteRule, itemFichaPropiaRule,
                estadoFichaPerfilExisteRule, estadoFichaPerfilEnTerminalRule);
        inOrder.verify(itemFichaPerfilExisteRule)
                .validar(new ExistenciaItemFichaPerfil(item, true));
        inOrder.verify(itemFichaPropiaRule).validar(new PropiedadFicha(fichaPerfil, estudiante, true));
        inOrder.verify(estadoFichaPerfilExisteRule)
                .validar(new ExistenciaEstadoFichaPerfil(fichaPerfil, true));
        inOrder.verify(estadoFichaPerfilEnTerminalRule)
                .validar(new EstadoActualFicha(fichaPerfil, estadoActual.getEstadoFicha()));
    }

    @Test
    void noDebeAplicarLasReglasDependientesDeLaFicha_cuandoElItemNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(itemFichaPerfilExisteRule).validar(new ExistenciaItemFichaPerfil(item, false));

        // Act
        assertThatThrownBy(() -> validator.validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(),
                false, false, EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        // Assert
        verify(itemFichaPropiaRule, never()).validar(org.mockito.ArgumentMatchers.any());
        verify(estadoFichaPerfilEnTerminalRule, never()).validar(org.mockito.ArgumentMatchers.any());
    }
}
