package com.arquisoft.artefactos.domain.estudianteartefacto.event;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteArtefactoRemovidoEventTest {

    @Test
    void debeCargarTemaDocumentoEstudianteYAsesor_cuandoSeConstruye() {
        // Arrange
        var estudiante = new ContactoEstudiante("Laura Gomez", "laura@uco.edu.co");
        var asesor = new ContactoAsesor("Ana Perez", "ana.perez@uco.edu.co");

        // Act
        var evento = new EstudianteArtefactoRemovidoEvent("Anteproyecto", estudiante, asesor);

        // Assert
        assertThat(EstudianteArtefactoRemovidoEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Artefactos.ESTUDIANTE_ARTEFACTO_REMOVIDO);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getEstudiante()).isEqualTo(estudiante);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
    }
}
