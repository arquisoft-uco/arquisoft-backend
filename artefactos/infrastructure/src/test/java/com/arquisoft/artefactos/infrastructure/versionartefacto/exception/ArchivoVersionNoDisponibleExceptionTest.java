package com.arquisoft.artefactos.infrastructure.versionartefacto.exception;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
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
        assertThat(excepcion.getCodigoError()).isEqualTo(ArtefactosCodes.VersionArtefacto.ARCHIVO_NO_DISPONIBLE);
        assertThat(excepcion.getError().getMessage()).doesNotContain(version.toString(), key);
        assertThat(excepcion.getMessage()).contains(version.toString(), key);
    }
}
