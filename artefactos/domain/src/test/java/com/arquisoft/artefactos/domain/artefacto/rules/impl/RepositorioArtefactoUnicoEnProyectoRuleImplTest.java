package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositoriosProyecto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

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
    void debePasar_cuandoRepositoriosDistintosYNoAsignados() {
        // Arrange
        var disponibilidad = new DisponibilidadRepositoriosProyecto(
                List.of(version(UtilUUID.generarNuevoUUID()), version(UtilUUID.generarNuevoUUID())),
                List.of(UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoDosVersionesSonDelMismoRepositorio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadRepositoriosProyecto(
                List.of(version(repositorio), version(repositorio)), List.of());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(RepositorioArtefactoYaAsignadoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.Artefacto.REPOSITORIO_YA_ASIGNADO);
    }

    @Test
    void debeLanzar_cuandoElProyectoYaTieneElRepositorio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadRepositoriosProyecto(
                List.of(version(repositorio)), List.of(repositorio));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(RepositorioArtefactoYaAsignadoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.Artefacto.REPOSITORIO_YA_ASIGNADO);
    }
}
