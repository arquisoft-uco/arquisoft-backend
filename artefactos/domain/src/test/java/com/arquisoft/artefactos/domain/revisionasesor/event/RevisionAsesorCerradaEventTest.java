package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.model.VersionCerradaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorCerradaEventTest {

    private static VersionCerradaEvento version() {
        return new VersionCerradaEvento(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "versiones/a/v1",
                Instant.parse("2026-11-10T10:00:00Z"), UUID.randomUUID(), UUID.randomUUID(), "Repositorio");
    }

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var asesor = new ContactoAsesor("Ana", "ana@uco.edu.co");
        var estudiantes = List.of(new ContactoEstudiante("Luis", "luis@uco.edu.co"));
        var version = version();

        // Act
        var evento = new RevisionAsesorCerradaEvent("Anteproyecto", 3, version, asesor, estudiantes);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.REVISION_ASESOR_CERRADA);
        assertThat(evento.getTemaEvento()).isEqualTo("artefactos.revision_asesor.cerrada");
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getNumeroVersion()).isEqualTo(3);
        assertThat(evento.getProyectoGrado()).isEqualTo(version.proyectoGrado());
        assertThat(evento.getArtefacto()).isEqualTo(version.artefacto());
        assertThat(evento.getVersionArtefacto()).isEqualTo(version.versionArtefacto());
        assertThat(evento.getUriArchivo()).isEqualTo(version.uriArchivo());
        assertThat(evento.getFechaVersion()).isEqualTo(version.fechaVersion());
        assertThat(evento.getVersionRepositorioArtefacto()).isEqualTo(version.versionRepositorioArtefacto());
        assertThat(evento.getRepositorio()).isEqualTo(version.repositorio());
        assertThat(evento.getNombreRepositorio()).isEqualTo(version.nombreRepositorio());
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
        assertThat(evento.getIdEvento()).isNotBlank();
    }

    @Test
    void debeCopiarListaDeEstudiantes_cuandoLaOriginalCambiaDespues() {
        // Arrange
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(new ContactoEstudiante("Luis", "luis@uco.edu.co"));
        var evento = new RevisionAsesorCerradaEvent(
                "Anteproyecto", 1, version(), new ContactoAsesor("Ana", "ana@uco.edu.co"), estudiantes);

        // Act
        estudiantes.add(new ContactoEstudiante("Pedro", "pedro@uco.edu.co"));

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThatThrownBy(() -> evento.getEstudiantes().add(new ContactoEstudiante("X", "x@uco.edu.co")))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
