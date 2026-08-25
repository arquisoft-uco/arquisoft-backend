package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilDelItemFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private FichaPerfilDelItemFinderImpl finder;

    @Test
    void debeDevolverLaFichaDelItem_cuandoElItemExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.obtenerFichaPerfilId(itemId)).thenReturn(Optional.of(fichaId));

        // Act
        Optional<UUID> resultado = finder.obtener(itemId);

        // Assert
        assertThat(resultado).contains(fichaId);
    }

    @Test
    void debeDevolverVacio_cuandoElItemNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.obtenerFichaPerfilId(itemId)).thenReturn(Optional.empty());

        // Act
        Optional<UUID> resultado = finder.obtener(itemId);

        // Assert — el finder nunca lanza por "no encontrado"; eso lo decide la rule
        assertThat(resultado).isEmpty();
    }
}
