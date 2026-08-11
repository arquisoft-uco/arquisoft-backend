package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.secondaryport.ItemFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilDelItemFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private FichaPerfilDelItemFinderImpl finder;

    @Test
    void debeRetornarElFichaPerfilId_cuandoElItemExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID fichaPerfil = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.obtenerFichaPerfilId(item)).thenReturn(Optional.of(fichaPerfil));

        // Act
        UUID resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarExcepcion_cuandoElItemNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.obtenerFichaPerfilId(item)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> finder.obtener(item))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(item.toString());
    }
}
