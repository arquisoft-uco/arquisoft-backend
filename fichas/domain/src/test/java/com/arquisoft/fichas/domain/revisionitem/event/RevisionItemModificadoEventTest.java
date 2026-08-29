package com.arquisoft.fichas.domain.revisionitem.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemModificadoEventTest {

    @Test
    void debeExponerLosDatosDeLaRevision_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act
        var evento = new RevisionItemModificadoEvent(itemId, "VISUALIZADA", "Visualizada");

        // Assert
        assertThat(evento.getItemId()).isEqualTo(itemId);
        assertThat(evento.getEstadoRevisionId()).isEqualTo("VISUALIZADA");
        assertThat(evento.getEstadoRevisionNombre()).isEqualTo("Visualizada");
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        var evento = new RevisionItemModificadoEvent(UUID.randomUUID(), "NUEVA", "Nueva");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.revision_item.modificado");
        assertThat(evento.getTipoEvento()).isEqualTo("RevisionItemModificadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new RevisionItemModificadoEvent(UUID.randomUUID(), "NUEVA", "Nueva");

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
