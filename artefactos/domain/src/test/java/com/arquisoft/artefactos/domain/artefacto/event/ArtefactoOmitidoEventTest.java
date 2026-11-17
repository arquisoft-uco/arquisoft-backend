package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoOmitidoEventTest {

    @Test
    void debeExponerTemaYCopiarLaLista_cuandoSeConstruye() {
        // Arrange
        var estudiantes = new ArrayList<>(List.of(new ContactoEstudiante("Ana", "ana@uco.edu.co")));
        var asesor = new ContactoAsesor("Luis", "luis@uco.edu.co");
        var artefacto = UUID.randomUUID();
        var proyectoGrado = UUID.randomUUID();

        // Act
        var evento = new ArtefactoOmitidoEvent("Documento", estudiantes, asesor, artefacto, proyectoGrado);
        estudiantes.clear();

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.ARTEFACTO_OMITIDO);
        assertThat(evento.getDocumento()).isEqualTo("Documento");
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getArtefacto()).isEqualTo(artefacto);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThatThrownBy(() -> evento.getEstudiantes().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
