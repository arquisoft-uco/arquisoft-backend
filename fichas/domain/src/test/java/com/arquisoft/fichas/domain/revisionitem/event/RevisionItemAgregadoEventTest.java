package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemAgregadoEventTest {

    private static final List<ContactoEstudiante> ESTUDIANTES =
            List.of(new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"));

    @Test
    void debeExponerLosDatosDeLaRevision_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID revisionItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Instant fechaCreacion = Instant.now();

        // Act
        var evento = new RevisionItemAgregadoEvent(
                revisionItemId, itemId, "NUEVA", "Nueva", fechaCreacion,
                "Sistema de gestión", ESTUDIANTES);

        // Assert
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(evento.getItemId()).isEqualTo(itemId);
        assertThat(evento.getEstadoRevisionId()).isEqualTo("NUEVA");
        assertThat(evento.getEstadoRevisionNombre()).isEqualTo("Nueva");
        assertThat(evento.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).isEqualTo(ESTUDIANTES);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        var evento = new RevisionItemAgregadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", "Nueva", Instant.now(),
                "Sistema de gestión", ESTUDIANTES);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.revision_item.agregado");
        assertThat(evento.getTipoEvento()).isEqualTo("RevisionItemAgregadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new RevisionItemAgregadoEvent(
                UUID.randomUUID(), UUID.randomUUID(), "NUEVA", "Nueva", Instant.now(),
                "Sistema de gestión", ESTUDIANTES);

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
