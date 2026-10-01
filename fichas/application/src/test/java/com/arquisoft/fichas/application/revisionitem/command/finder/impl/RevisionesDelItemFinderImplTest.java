package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionesDelItemFinderImplTest {

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @InjectMocks
    private RevisionesDelItemFinderImpl finder;

    @Test
    void debeTrasladarElConteoDeRevisiones_cuandoElItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(revisionItemOutputPort.contarPorItem(itemId)).thenReturn(3L);

        // Act & Assert
        assertThat(finder.obtener(itemId)).isEqualTo(3L);
    }

    @Test
    void debeTrasladarCero_cuandoElItemNoTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(revisionItemOutputPort.contarPorItem(itemId)).thenReturn(0L);

        // Act & Assert
        assertThat(finder.obtener(itemId)).isZero();
    }
}
