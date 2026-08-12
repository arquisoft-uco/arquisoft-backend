package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionItemCommandOutputAdapterTest {

    @Mock
    private RevisionItemCommandRepository repository;

    @InjectMocks
    private RevisionItemCommandOutputAdapter adapter;

    @Test
    void debeRetornarCount_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(repository.countByItemId(itemId)).thenReturn(3L);

        // Act
        long resultado = adapter.contarPorItem(itemId);

        // Assert
        assertThat(resultado).isEqualTo(3L);
        verify(repository, times(1)).countByItemId(itemId);
    }

    @Test
    void debeRetornarCero_cuandoItemSinRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(repository.countByItemId(itemId)).thenReturn(0L);

        // Act
        long resultado = adapter.contarPorItem(itemId);

        // Assert
        assertThat(resultado).isZero();
        verify(repository, times(1)).countByItemId(itemId);
    }
}
