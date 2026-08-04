package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
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
class ItemFichaPerfilFinderImplTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private ItemFichaPerfilFinderImpl finder;

    @Test
    void debeRetornarElItem_cuandoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ItemFichaPerfilAggregate item = ItemFichaPerfilAggregate.reconstruir(
                itemId, UUID.randomUUID(), TipoItem.OBJETIVO_GENERAL, "Contenido de prueba");
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.of(item));

        // Act
        ItemFichaPerfilAggregate resultado = finder.obtener(itemId);

        // Assert
        assertThat(resultado).isSameAs(item);
    }

    @Test
    void debeLanzarExcepcion_cuandoNoExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.buscarPorId(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> finder.obtener(itemId))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(itemId.toString());
    }
}
