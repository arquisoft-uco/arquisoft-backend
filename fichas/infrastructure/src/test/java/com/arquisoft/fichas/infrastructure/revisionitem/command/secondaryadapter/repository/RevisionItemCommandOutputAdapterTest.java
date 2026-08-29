package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionItemCommandOutputAdapterTest {

    @Mock
    private RevisionItemCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RevisionItemCommandOutputAdapter adapter;

    @Test
    void debeRegistrarRevision_cuandoSeInvoca() {
        // Arrange
        var revision = new RevisionItemEntity(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", Instant.now());

        // Act
        adapter.registrarRevision(revision);

        // Assert
        var captor = ArgumentCaptor.forClass(RevisionItemJpaEntity.class);
        verify(repository, times(1)).save(captor.capture());

        var jpaEntity = captor.getValue();
        assertThat(jpaEntity.getId()).isEqualTo(revision.id());
        assertThat(jpaEntity.getItemId()).isEqualTo(revision.item());
        assertThat(jpaEntity.getEstadoRevision().getId()).isEqualTo(revision.estadoRevision());
        assertThat(jpaEntity.getFechaCreacion()).isEqualTo(revision.fechaCreacion());
    }

    @Test
    void debeActualizarEstado_cuandoSeInvoca() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act
        adapter.actualizarEstado(itemId, "VISUALIZADA");

        // Assert
        var captor = ArgumentCaptor.forClass(EstadoRevisionJpaEntity.class);
        verify(repository, times(1)).actualizarEstadoRevision(eq(itemId), captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("VISUALIZADA");
        verify(logger, times(1)).debug(anyString(), eq(itemId));
    }

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
