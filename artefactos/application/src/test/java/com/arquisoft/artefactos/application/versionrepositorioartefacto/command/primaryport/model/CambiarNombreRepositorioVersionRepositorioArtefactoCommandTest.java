package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarNombreRepositorioVersionRepositorioArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarNombreRepositorioVersionRepositorioArtefactoCommand.crear(
                repositorio.toString(), "  Repositorio nuevo  ");

        // Assert
        assertThat(command.repositorioArtefacto()).isEqualTo(repositorio);
        assertThat(command.nombreRepositorio()).isEqualTo("Repositorio nuevo");
    }

    @Test
    void debeRechazar_cuandoRepositorioNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarNombreRepositorioVersionRepositorioArtefactoCommand.crear(
                "no-es-uuid", "Repositorio"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> assertThat(error.campo())
                                .isEqualTo(ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO)));
    }

    @Test
    void debeRechazar_cuandoNombreVacio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> CambiarNombreRepositorioVersionRepositorioArtefactoCommand.crear(repositorio, "   "))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_REQUERIDO)));
    }
}
