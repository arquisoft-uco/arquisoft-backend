package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarArchivoVersionRepositorioArtefactoValidatorImplTest {

    private final DescargarArchivoVersionRepositorioArtefactoValidatorImpl validator =
            new DescargarArchivoVersionRepositorioArtefactoValidatorImpl();

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Arrange
        var existencia = new ExistenciaVersionRepositorioArtefacto(
                UtilUUID.generarNuevoUUID(), ArchivoVersionDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void noDebeLanzar_cuandoLaVersionExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaVersionRepositorioArtefacto(
                version, ArchivoVersionDomain.reconstruir("versiones/" + version, "formato.pdf"));

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia)).doesNotThrowAnyException();
    }
}
