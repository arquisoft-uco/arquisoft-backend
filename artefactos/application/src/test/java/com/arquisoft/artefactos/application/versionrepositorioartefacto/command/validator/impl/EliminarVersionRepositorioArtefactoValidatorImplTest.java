package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
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
        assertThatThrownBy(() -> validator.validar(eliminacion, VersionRepositorioArtefactoDomain.VACIO, true))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarEnUso_cuandoEstaEnUso() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, version, true))
                .isInstanceOf(VersionRepositorioArtefactoEnUsoException.class);
    }

    @Test
    void debeNoLanzar_cuandoExisteYNoEstaEnUso() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(id);
        var version = crearVersion(id);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, version, false)).doesNotThrowAnyException();
    }

    private VersionRepositorioArtefactoDomain crearVersion(UUID id) {
        return VersionRepositorioArtefactoDomain.reconstruir(id, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }
}
