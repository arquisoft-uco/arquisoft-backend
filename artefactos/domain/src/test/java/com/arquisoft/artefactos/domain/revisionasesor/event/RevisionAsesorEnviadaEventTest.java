package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorEnviadaEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var asesor = new ContactoAsesor("Ana", "ana@uco.edu.co");
        var estudiantes = List.of(new ContactoEstudiante("Luis", "luis@uco.edu.co"));

        // Act
        var evento = new RevisionAsesorEnviadaEvent("Anteproyecto", 3, asesor, estudiantes);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.REVISION_ASESOR_ENVIADA);
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getNumeroVersion()).isEqualTo(3);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
        assertThat(evento.getIdEvento()).isNotBlank();
    }

    @Test
    void debeCopiarListaDeEstudiantes_cuandoLaOriginalCambiaDespues() {
        // Arrange
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(new ContactoEstudiante("Luis", "luis@uco.edu.co"));
        var evento = new RevisionAsesorEnviadaEvent(
                "Anteproyecto", 1, new ContactoAsesor("Ana", "ana@uco.edu.co"), estudiantes);

        // Act
        estudiantes.add(new ContactoEstudiante("Pedro", "pedro@uco.edu.co"));

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThatThrownBy(() -> evento.getEstudiantes().add(new ContactoEstudiante("X", "x@uco.edu.co")))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
