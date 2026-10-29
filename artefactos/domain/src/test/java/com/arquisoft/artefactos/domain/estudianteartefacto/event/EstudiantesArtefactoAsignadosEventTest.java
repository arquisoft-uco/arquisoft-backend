package com.arquisoft.artefactos.domain.estudianteartefacto.event;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EstudiantesArtefactoAsignadosEventTest {

    @Test
    void debeCargarDocumentoContactosYAsesorConCopiaInmutable_cuandoSeConstruye() {
        // Arrange
        var documento = "Anteproyecto";
        var estudiantes = new ArrayList<>(List.of(new ContactoEstudiante("Laura Gomez", "laura@uco.edu.co")));
        var asesor = new ContactoAsesor("Ana Perez", "ana.perez@uco.edu.co");

        // Act
        var evento = new EstudiantesArtefactoAsignadosEvent(documento, estudiantes, asesor);
        estudiantes.clear();

        // Assert
        assertThat(EstudiantesArtefactoAsignadosEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Artefactos.ESTUDIANTES_ARTEFACTO_ASIGNADOS);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getEstudiantes())
                .containsExactly(new ContactoEstudiante("Laura Gomez", "laura@uco.edu.co"));
        assertThat(evento.getAsesor()).isEqualTo(asesor);
    }
}
