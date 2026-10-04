package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper.RevisionItemJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

    @Test
    void debeRetornarLaRevision_cuandoExistePorId() {
        // Arrange
        var revision = new RevisionItemEntity(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", Instant.now());
        var jpaEntity = RevisionItemJpaMapper.toJpaEntity(revision);
        when(repository.findById(revision.id())).thenReturn(Optional.of(jpaEntity));

        // Act
        var resultado = adapter.buscarPorId(revision.id());

        // Assert
        assertThat(resultado).contains(revision);
        verify(repository, times(1)).findById(revision.id());
    }

    @Test
    void debeRetornarVacio_cuandoNoExistePorId() {
        // Arrange
        var revisionItemId = UUID.randomUUID();
        when(repository.findById(revisionItemId)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.buscarPorId(revisionItemId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findById(revisionItemId);
    }

    @Test
    void debeDelegarSinLogear_cuandoObtienePertenencia() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var entity = new PertenenciaRevisionItemEntity(UUID.randomUUID(), true, "NUEVA");
        when(repository.obtenerPertenencia(revisionItem, estudiante)).thenReturn(Optional.of(entity));

        // Act
        var resultado = adapter.obtenerPertenencia(revisionItem, estudiante);

        // Assert
        assertThat(resultado).containsSame(entity);
        verify(repository, times(1)).obtenerPertenencia(revisionItem, estudiante);
        verifyNoInteractions(logger);
    }

    @Test
    void debeDelegarYLogearEnDebug_cuandoActualizaElEstado() {
        // Arrange
        var revisionItem = UUID.randomUUID();

        // Act
        adapter.actualizarEstado(revisionItem, "NUEVA", "VISUALIZADA");

        // Assert
        verify(repository, times(1)).actualizarEstado(revisionItem, "NUEVA", "VISUALIZADA");
        verify(logger, times(1)).debug(RevisionItemKey.LOG_ACTUALIZADO, revisionItem, "VISUALIZADA");
    }

    @Test
    void debeDelegarSinLogear_cuandoObtieneAsesoria() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var entity = new AsesoriaRevisionItemEntity(UUID.randomUUID(), UUID.randomUUID(), "NUEVA");
        when(repository.obtenerAsesoria(revisionItem)).thenReturn(Optional.of(entity));

        // Act
        var resultado = adapter.obtenerAsesoria(revisionItem);

        // Assert
        assertThat(resultado).containsSame(entity);
        verify(repository, times(1)).obtenerAsesoria(revisionItem);
        verifyNoInteractions(logger);
    }

    @Test
    void debeDelegarYLogearEnDebug_cuandoRemueveLaRevision() {
        // Arrange
        var revisionItem = UUID.randomUUID();

        // Act
        adapter.removerRevision(revisionItem);

        // Assert
        verify(repository, times(1)).removerPorId(revisionItem);
        verify(logger, times(1)).debug(RevisionItemKey.LOG_ELIMINADO, revisionItem);
    }
}
