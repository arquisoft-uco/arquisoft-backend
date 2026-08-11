package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.secondaryport.ItemFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemTipoNoDuplicadoRuleImplTest {

    @Mock
    private ItemFichaPerfilOutputPort puerto;

    @InjectMocks
    private ItemTipoNoDuplicadoRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoLaReglaNoSeCumple() {
        // Arrange
        var entrada = ItemFichaPerfilDomain.crear(UUID.randomUUID(), "OBJETIVO_GENERAL", "Contenido de prueba");
        when(puerto.existePorFichaYTipoItem(entrada.getFichaPerfilId(), entrada.getTipoItem().getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ItemTipoDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoLaReglaSeCumple() {
        // Arrange
        var entrada = ItemFichaPerfilDomain.crear(UUID.randomUUID(), "OBJETIVO_GENERAL", "Contenido de prueba");
        when(puerto.existePorFichaYTipoItem(entrada.getFichaPerfilId(), entrada.getTipoItem().getId())).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
