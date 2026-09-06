package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemModificadoEventTest {

    private static final List<ContactoEstudiante> ESTUDIANTES =
            List.of(new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"));

    @Test
    void debeExponerLosDatosDeLaRevision_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act
        var evento = new RevisionItemModificadoEvent(
                itemId, "VISUALIZADA", "Visualizada", "Sistema de gestión", ESTUDIANTES);

        // Assert
        assertThat(evento.getItemId()).isEqualTo(itemId);
        assertThat(evento.getEstadoRevisionId()).isEqualTo("VISUALIZADA");
        assertThat(evento.getEstadoRevisionNombre()).isEqualTo("Visualizada");
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).isEqualTo(ESTUDIANTES);
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        var evento = new RevisionItemModificadoEvent(
                UUID.randomUUID(), "NUEVA", "Nueva", "Sistema de gestión", ESTUDIANTES);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.revision_item.modificado");
        assertThat(evento.getTipoEvento()).isEqualTo("RevisionItemModificadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new RevisionItemModificadoEvent(
                UUID.randomUUID(), "NUEVA", "Nueva", "Sistema de gestión", ESTUDIANTES);

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
