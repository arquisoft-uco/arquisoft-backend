package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void debeDelegarObtenerContexto_cuandoSeConsultaLaObservacion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var contexto = Optional.of(new ContextoObservacionItemEntity(
                UtilUUID.generarNuevoUUID(), "NUEVA", UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));
        when(repository.obtenerContexto(observacionItem)).thenReturn(contexto);

        // Act
        var resultado = adapter.obtenerContexto(observacionItem);

        // Assert
        assertThat(resultado).isSameAs(contexto);
        verify(repository, times(1)).obtenerContexto(observacionItem);
    }

    @Test
    void debeDelegarContarOtrasIguales_cuandoSeConsultaLaRevision() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        when(repository.contarOtrasIgualesEnRevision(observacionItem, "Texto nuevo")).thenReturn(3L);

        // Act
        var resultado = adapter.contarOtrasIgualesEnRevision(observacionItem, "Texto nuevo");

        // Assert
        assertThat(resultado).isEqualTo(3L);
        verify(repository, times(1)).contarOtrasIgualesEnRevision(observacionItem, "Texto nuevo");
    }

    @Test
    void debeActualizarYRegistrarDebug_cuandoActualizaLaObservacion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();

        // Act
        adapter.actualizarObservacion(observacionItem, "Texto nuevo");

        // Assert
        verify(repository, times(1)).actualizarObservacion(observacionItem, "Texto nuevo");
        verify(logger).debug(eq(ObservacionItemKey.LOG_GUARDADA), eq(observacionItem));
    }

    @Test
    void debeRemoverYRegistrarDebug_cuandoRemueveLaObservacion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();

        // Act
        adapter.removerObservacion(observacionItem);

        // Assert
        verify(repository, times(1)).removerPorId(observacionItem);
        verify(logger).debug(eq(ObservacionItemKey.LOG_ELIMINADA), eq(observacionItem));
    }
}
