package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorDomainTest {

    @Test
    void debeReconstruirYComparar_cuandoSeConsultaElEstado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var revision = RevisionAsesorDomain.reconstruir(id, version, EstadoRevisionAsesor.PENDIENTE);

        // Assert
        assertThat(revision.getId()).isEqualTo(id);
        assertThat(revision.getVersionArtefacto()).isEqualTo(version);
        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE);
        assertThat(revision.tieneEstado(EstadoRevisionAsesor.PENDIENTE)).isTrue();
        assertThat(revision.tieneEstado(EstadoRevisionAsesor.RESUELTA)).isFalse();
        assertThat(revision.esVacio()).isFalse();
    }

    @Test
    void vacio_debeSerVacio() {
        // Act & Assert
        assertThat(RevisionAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(RevisionAsesorDomain.VACIO.getId())).isTrue();
        assertThat(RevisionAsesorDomain.VACIO.getEstado()).isEqualTo(EstadoRevisionAsesor.VACIO);
    }
}
