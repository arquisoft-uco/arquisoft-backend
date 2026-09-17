package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionItemCommandOutputAdapterTest {

    @Mock
    private ObservacionItemCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ObservacionItemCommandOutputAdapter adapter;

    @Test
    void debeRegistrarObservacion_cuandoSeInvoca() {
        // Arrange
        var observacion = new ObservacionItemEntity(
                UUID.randomUUID(), UUID.randomUUID(), "Observación válida", "PENDIENTE");

        // Act
        adapter.registrarObservacion(observacion);

        // Assert
        var captor = ArgumentCaptor.forClass(ObservacionItemJpaEntity.class);
        verify(repository, times(1)).save(captor.capture());

        var jpaEntity = captor.getValue();
        assertThat(jpaEntity.getId()).isEqualTo(observacion.id());
        assertThat(jpaEntity.getRevisionItemId()).isEqualTo(observacion.revisionItem());
        assertThat(jpaEntity.getObservacion()).isEqualTo(observacion.observacion());
        assertThat(jpaEntity.getEstadoObservacionRevision().getId()).isEqualTo(observacion.estadoObservacionRevision());
    }

    @Test
    void debeRetornarCount_cuandoDelegaAlRepositorio() {
        // Arrange
        var revisionItemId = UUID.randomUUID();
        when(repository.countByRevisionItemIdAndObservacion(revisionItemId, "Observación válida"))
                .thenReturn(1L);

        // Act
        var resultado = adapter.contarPorRevisionYObservacion(revisionItemId, "Observación válida");

        // Assert
        assertThat(resultado).isEqualTo(1L);
        verify(repository, times(1)).countByRevisionItemIdAndObservacion(revisionItemId, "Observación válida");
    }
}
