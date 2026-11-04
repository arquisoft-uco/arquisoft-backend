package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarArchivoVersionRepositorioArtefactoQueryTest {

    @Test
    void debeCrear_cuandoLaVersionEsValida() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var query = DescargarArchivoVersionRepositorioArtefactoQuery.crear(version);

        // Assert
        assertThat(query.version()).isEqualTo(version);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoLaVersionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> DescargarArchivoVersionRepositorioArtefactoQuery.crear(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
