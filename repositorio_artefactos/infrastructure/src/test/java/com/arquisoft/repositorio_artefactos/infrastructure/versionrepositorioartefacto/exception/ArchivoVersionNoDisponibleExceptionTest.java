package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.exception;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoVersionNoDisponibleExceptionTest {

    @Test
    void debeDejarVersionYKeyFueraDelMessage_yEnElDetalle() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var key = "versiones/" + version;

        // Act
        var excepcion = new ArchivoVersionNoDisponibleException(version, key);

        // Assert
        assertThat(excepcion.getCodigoError())
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.ARCHIVO_NO_DISPONIBLE);
        assertThat(excepcion.getError().getMessage())
                .doesNotContain(version.toString())
                .doesNotContain(key);
        assertThat(excepcion.getError().getDetalle()).contains(version.toString()).contains(key);
    }
}
