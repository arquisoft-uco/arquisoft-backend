package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrear_cuandoUuidValido() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarVersionRepositorioArtefactoCommand.crear(version.toString());

        // Assert
        assertThat(command.version()).isEqualTo(version);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUuidMalFormado() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarVersionRepositorioArtefactoCommand.crear("no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(e -> e.campo())
                            .containsExactly(RepositorioArtefactosFields.VersionRepositorioArtefacto.VERSION);
                    assertThat(errores).extracting(e -> e.codigoError())
                            .containsExactly(RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_REQUERIDA);
                });
    }
}
