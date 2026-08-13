package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoItemEnFichaExisteFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private TipoItemEnFichaExisteFinderImpl finder;

    @Test
    void debeTrasladarQueElTipoYaEstaEnLaFicha_cuandoElPuertoResponde() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        var item = AgregacionItemFichaPerfilDomain.crear(
                ItemFichaPerfilDomain.crear(fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido"), UUID.randomUUID()).getItem();
        when(itemFichaPerfilOutputPort.existePorFichaYTipoItem(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId())).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(item)).isTrue();
    }

    @Test
    void debeTrasladarQueElTipoEstaLibre_cuandoElPuertoResponde() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        var item = AgregacionItemFichaPerfilDomain.crear(
                ItemFichaPerfilDomain.crear(fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido"), UUID.randomUUID()).getItem();
        when(itemFichaPerfilOutputPort.existePorFichaYTipoItem(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId())).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(item)).isFalse();
    }
}
