package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionItemFinderImplTest {

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @InjectMocks
    private RevisionItemFinderImpl revisionItemFinder;

    @Test
    void debeDelegarAlPuerto_cuandoLaRevisionExiste() {
        // Arrange
        var revisionItemId = UUID.randomUUID();
        var entity = new RevisionItemEntity(revisionItemId, UUID.randomUUID(), "NUEVA", Instant.now());
        when(revisionItemOutputPort.buscarPorId(revisionItemId)).thenReturn(Optional.of(entity));

        // Act
        var resultado = revisionItemFinder.obtener(revisionItemId);

        // Assert
        assertThat(resultado).contains(entity);
        verify(revisionItemOutputPort).buscarPorId(revisionItemId);
    }

    @Test
    void debeDelegarAlPuerto_cuandoLaRevisionNoExiste() {
        // Arrange
        var revisionItemId = UUID.randomUUID();
        when(revisionItemOutputPort.buscarPorId(revisionItemId)).thenReturn(Optional.empty());

        // Act
        var resultado = revisionItemFinder.obtener(revisionItemId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(revisionItemOutputPort).buscarPorId(revisionItemId);
    }
}
