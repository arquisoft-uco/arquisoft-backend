package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarRepositorioArtefactoCommandTest {

    @Test
    void debeCrearRecortandoTextos_cuandoElAdministradorEsUuidValido() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var command = GenerarRepositorioArtefactoCommand.crear("  Repo  ", "  Descripcion  ", administrador.toString());

        // Assert
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreVacioDescripcionLargaYAdministradorInvalido() {
        // Arrange
        var descripcionLarga = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> GenerarRepositorioArtefactoCommand.crear("   ", descripcionLarga, "no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.NOMBRE_REPOSITORIO);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.DESCRIPCION);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_DEMASIADO_LARGA);
                    });
                    assertThat(errores).anySatisfy(error -> assertThat(error.campo())
                            .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR));
                });
    }

    @Test
    void debeRespetarLosLimites_cuandoNombreEs100YDescripcionEs300PeroNombre101Falla() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID().toString();

        // Act
        var command = GenerarRepositorioArtefactoCommand.crear("n".repeat(100), "d".repeat(300), administrador);

        // Assert
        assertThat(command.nombreRepositorio()).hasSize(100);
        assertThat(command.descripcion()).hasSize(300);
        assertThatThrownBy(() -> GenerarRepositorioArtefactoCommand.crear(
                "n".repeat(101), "Descripcion", administrador))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO)));
    }
}
