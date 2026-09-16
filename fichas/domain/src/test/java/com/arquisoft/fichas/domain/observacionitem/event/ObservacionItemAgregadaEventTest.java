package com.arquisoft.fichas.domain.observacionitem.event;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemAgregadaEventTest {

    @Test
    void debeExponerLosDatosDeLaObservacion_cuandoSeConstruyeElEvento() {
        // Arrange
        var observacionItemId = UUID.randomUUID();
        var revisionItemId = UUID.randomUUID();

        // Act
        var evento = new ObservacionItemAgregadaEvent(
                observacionItemId, revisionItemId, "Observación", "PENDIENTE", "Pendiente");

        // Assert
        assertThat(evento.getObservacionItemId()).isEqualTo(observacionItemId);
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(evento.getObservacion()).isEqualTo("Observación");
        assertThat(evento.getEstadoObservacionRevisionId()).isEqualTo("PENDIENTE");
        assertThat(evento.getEstadoObservacionRevisionNombre()).isEqualTo("Pendiente");
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Fichas.OBSERVACION_ITEM_AGREGADA);
        assertThat(evento.getTipoEvento()).isEqualTo("ObservacionItemAgregadaEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new ObservacionItemAgregadaEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Observación", "PENDIENTE", "Pendiente");

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
