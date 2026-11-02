package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.DocumentoArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadDocumentoProyecto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentoArtefactoUnicoEnProyectoRuleImplTest {

    private final DocumentoArtefactoUnicoEnProyectoRuleImpl regla = new DocumentoArtefactoUnicoEnProyectoRuleImpl();

    private static VersionRepositorioArtefactoDomain version(UUID repositorio, String documento, int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), documento, numero, repositorio, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoElProyectoNoTieneVersiones() {
        // Arrange
        var disponibilidad = new DisponibilidadDocumentoProyecto(
                version(UtilUUID.generarNuevoUUID(), "Doc", 1), List.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElMismoRepositorioTieneOtroDocumento() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadDocumentoProyecto(
                version(repositorio, "Plantilla actor por comando", 1),
                List.of(version(repositorio, "Plantilla Event Storming", 1)));

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoOtroRepositorioTieneElMismoNombre() {
        // Arrange
        var disponibilidad = new DisponibilidadDocumentoProyecto(
                version(UtilUUID.generarNuevoUUID(), "Doc", 1),
                List.of(version(UtilUUID.generarNuevoUUID(), "Doc", 1)));

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElDocumentoYaEstaAsignado() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadDocumentoProyecto(
                version(repositorio, "Doc", 2), List.of(version(repositorio, "Doc", 1)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(DocumentoArtefactoYaAsignadoException.class)
                .satisfies(ex -> {
                    var error = ((DocumentoArtefactoYaAsignadoException) ex).getError();
                    assertThat(error.getCodigoError()).isEqualTo(ArtefactosCodes.Artefacto.DOCUMENTO_YA_ASIGNADO);
                    assertThat(error.getMessage()).contains("Doc", "Repo").doesNotContain(repositorio.toString());
                    assertThat(error.getDetalle()).contains(repositorio.toString());
                });
    }

    @Test
    void debeLanzar_cuandoElNombreSoloDifiereEnMayusculas() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadDocumentoProyecto(
                version(repositorio, "plantilla event storming", 1),
                List.of(version(repositorio, "Plantilla Event Storming", 1)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(DocumentoArtefactoYaAsignadoException.class);
    }
}
