package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemFichaPerfilExisteFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private ItemFichaPerfilExisteFinderImpl finder;

    @Test
    void debeTrasladarQueElItemExiste_cuandoElPuertoResponde() {
        // Arrange
        UUID item = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.existePorId(item)).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(item)).isTrue();
    }

    @Test
    void debeTrasladarQueElItemNoExiste_cuandoElPuertoResponde() {
        // Arrange
        UUID item = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.existePorId(item)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(item)).isFalse();
    }
}
