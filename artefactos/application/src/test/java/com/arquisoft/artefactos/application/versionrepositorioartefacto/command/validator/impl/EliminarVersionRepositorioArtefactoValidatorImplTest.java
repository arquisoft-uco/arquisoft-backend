package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarVersionRepositorioArtefactoValidatorImplTest {

    private final EliminarVersionRepositorioArtefactoValidatorImpl validator =
            new EliminarVersionRepositorioArtefactoValidatorImpl();

    @Test
    void debeLanzarNoEncontrada_cuandoLaVersionEsVacia() {
        // Arrange
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, VersionRepositorioArtefactoDomain.VACIO, 0, true))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoEsUltima_cuandoHayUnaMasReciente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id, 2);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, version, 3, false))
                .isInstanceOf(VersionRepositorioArtefactoNoEsUltimaParaEliminarException.class);
    }

    @Test
    void debeLanzarNoEsUltima_antesQueEnUso_cuandoAmbasFallan() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id, 2);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, version, 3, true))
                .isInstanceOf(VersionRepositorioArtefactoNoEsUltimaParaEliminarException.class);
    }

    @Test
    void debeLanzarEnUso_cuandoEsLaUltimaYEstaEnUso() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id, 3);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, version, 3, true))
                .isInstanceOf(VersionRepositorioArtefactoEnUsoException.class);
    }

    @Test
    void debeNoLanzar_cuandoEsLaUltimaYNoEstaEnUso() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id, 3);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, version, 3, false)).doesNotThrowAnyException();
    }

    private VersionRepositorioArtefactoDomain crearVersion(UUID id, int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(id, "Doc", numero, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }
}
