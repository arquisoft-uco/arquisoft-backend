package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositorioProyecto;
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

class RepositorioArtefactoUnicoEnProyectoRuleImplTest {

    private final RepositorioArtefactoUnicoEnProyectoRuleImpl regla = new RepositorioArtefactoUnicoEnProyectoRuleImpl();

    private static VersionRepositorioArtefactoDomain version(UUID repositorio) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Doc", 1, repositorio, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoElRepositorioNoEstaAsignado() {
        // Arrange
        var disponibilidad = new DisponibilidadRepositorioProyecto(
                version(UtilUUID.generarNuevoUUID()), List.of(UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElRepositorioYaEstaAsignado() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadRepositorioProyecto(version(repositorio), List.of(repositorio));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(RepositorioArtefactoYaAsignadoException.class)
                .satisfies(ex -> {
                    var error = ((RepositorioArtefactoYaAsignadoException) ex).getError();
                    assertThat(error.getCodigoError()).isEqualTo(ArtefactosCodes.Artefacto.REPOSITORIO_YA_ASIGNADO);
                    assertThat(error.getMessage()).contains("Repo").doesNotContain(repositorio.toString());
                    assertThat(error.getDetalle()).contains(repositorio.toString());
                });
    }
}
