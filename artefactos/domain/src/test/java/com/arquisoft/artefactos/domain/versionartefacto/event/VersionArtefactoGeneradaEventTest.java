package com.arquisoft.artefactos.domain.versionartefacto.event;

import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.ContactoAsesor;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoGeneradaEventTest {

    @Test
    void debeCargarDatosDelCorreo() {
        // Arrange
        var generacion = GeneracionVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var version = VersionArtefactoDomain.crear(generacion, 2);
        var asesor = new ContactoAsesor("Ana Perez", "ana@uco.edu.co");

        // Act
        var evento = new VersionArtefactoGeneradaEvent(version, "Formato A", asesor);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA);
        assertThat(evento.getVersionArtefacto()).isEqualTo(version.getId());
        assertThat(evento.getArtefacto()).isEqualTo(version.getArtefacto());
        assertThat(evento.getNumeroVersion()).isEqualTo(3);
        assertThat(evento.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(evento.getAsesor()).isEqualTo(asesor);
    }
}
