package com.arquisoft.fichas.domain.revisionitem.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemAgregadoEventTest {

    @Test
    void debeExponerLosDatosDeLaRevision_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID revisionItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Instant fechaCreacion = Instant.now();

        // Act
        var evento = new RevisionItemAgregadoEvent(
                revisionItemId, itemId, "NUEVA", "Nueva", fechaCreacion);

        // Assert
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(evento.getItemId()).isEqualTo(itemId);
        assertThat(evento.getEstadoRevisionId()).isEqualTo("NUEVA");
        assertThat(evento.getEstadoRevisionNombre()).isEqualTo("Nueva");
        assertThat(evento.getFechaCreacion()).isEqualTo(fechaCreacion);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        var evento = new RevisionItemAgregadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", "Nueva", Instant.now());

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.revision_item.agregado");
        assertThat(evento.getTipoEvento()).isEqualTo("RevisionItemAgregadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new RevisionItemAgregadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", "Nueva", Instant.now());

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
