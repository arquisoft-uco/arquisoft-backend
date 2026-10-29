package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorDomainTest {

    @Test
    void debeCrearPendienteConFecha_cuandoVersionValida() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var antes = Instant.now();

        // Act
        var revision = RevisionAsesorDomain.crear(version);

        // Assert
        assertThat(UtilUUID.esPorDefecto(revision.getId())).isFalse();
        assertThat(revision.getVersionArtefacto()).isEqualTo(version);
        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE);
        assertThat(revision.getFechaCreacion()).isAfterOrEqualTo(antes);
        assertThat(revision.esVacio()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoVersionNula() {
        // Act & Assert
        assertThatThrownBy(() -> RevisionAsesorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .contains(ArtefactosFields.RevisionAsesor.VERSION_ARTEFACTO);
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.RevisionAsesor.VERSION_ARTEFACTO_REQUERIDA);
                });
    }

    @Test
    void debeReconstruirYComparar_cuandoSeConsultaElEstado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-28T10:00:00Z");

        // Act
        var revision = RevisionAsesorDomain.reconstruir(id, version, EstadoRevisionAsesor.PENDIENTE, fecha);

        // Assert
        assertThat(revision.getId()).isEqualTo(id);
        assertThat(revision.getVersionArtefacto()).isEqualTo(version);
        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE);
        assertThat(revision.getFechaCreacion()).isEqualTo(fecha);
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
        assertThat(RevisionAsesorDomain.VACIO.getFechaCreacion()).isEqualTo(UtilFecha.VACIO);
    }
}
