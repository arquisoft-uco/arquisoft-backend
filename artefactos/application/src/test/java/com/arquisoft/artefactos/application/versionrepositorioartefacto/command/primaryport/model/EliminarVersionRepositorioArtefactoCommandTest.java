package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrear_cuandoVersionValida() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarVersionRepositorioArtefactoCommand.crear(version);

        // Assert
        assertThat(command.version()).isEqualTo(version);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoVersionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarVersionRepositorioArtefactoCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(e -> e.campo())
                            .containsExactly(ArtefactosFields.VersionRepositorioArtefacto.ID);
                    assertThat(errores).extracting(e -> e.codigoError())
                            .containsExactly(ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO);
                });
    }
}
