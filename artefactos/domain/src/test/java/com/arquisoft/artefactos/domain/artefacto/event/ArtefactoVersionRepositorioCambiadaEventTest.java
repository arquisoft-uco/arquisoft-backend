package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoVersionRepositorioCambiadaEventTest {

    @Test
    void debeExponerTemaYCopiarLaLista_cuandoSeConstruye() {
        // Arrange
        var estudiantes = new ArrayList<>(List.of(new ContactoEstudiante("Ana", "ana@uco.edu.co")));
        var asesor = new ContactoAsesor("Luis", "luis@uco.edu.co");

        // Act
        var evento = new ArtefactoVersionRepositorioCambiadaEvent("Documento", 1, 2, estudiantes, asesor);
        estudiantes.clear();

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.ARTEFACTO_VERSION_REPOSITORIO_CAMBIADA);
        assertThat(evento.getDocumento()).isEqualTo("Documento");
        assertThat(evento.getVersionAnterior()).isEqualTo(1);
        assertThat(evento.getVersionNueva()).isEqualTo(2);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThatThrownBy(() -> evento.getEstudiantes().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
