package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRepositorioArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarRepositorioArtefactoCommand.crear(
                repositorio, "  Repo  ", "  Descripcion  ", administrador);

        // Assert
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreYDescripcionVacios() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarRepositorioArtefactoCommand.crear(null, "   ", "", administrador))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ID);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_REQUERIDO));
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_REQUERIDA));
                });
    }

    @Test
    void debeRechazar_cuandoAdministradorEsNulo() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarRepositorioArtefactoCommand.crear(repositorio, "Repo", "Desc", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> {
                            assertThat(error.campo())
                                    .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR);
                            assertThat(error.codigoError())
                                    .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO);
                        }));
    }

    @Test
    void debeRechazar_cuandoNombreYDescripcionSuperanElMaximo() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarRepositorioArtefactoCommand.crear(
                repositorio, "n".repeat(101), "d".repeat(301), administrador))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO));
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_DEMASIADO_LARGA));
                });
    }
}
