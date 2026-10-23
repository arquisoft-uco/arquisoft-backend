package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificacionRepositorioArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                repositorio, "  Repo Nuevo  ", "  Descripcion nueva  ", administrador);

        // Assert
        assertThat(modificacion.getRepositorio()).isEqualTo(repositorio);
        assertThat(modificacion.getNombreRepositorio()).isEqualTo("Repo Nuevo");
        assertThat(modificacion.getDescripcion()).isEqualTo("Descripcion nueva");
        assertThat(modificacion.getAdministrador()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionRepositorioArtefactoDomain.crear(null, "  ", " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ID);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ID_REQUERIDO);
                    });
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
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_REQUERIDA);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO);
                    });
                });
    }

    @Test
    void debeRechazar_cuandoNombreYDescripcionSuperanElMaximo() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificacionRepositorioArtefactoDomain.crear(
                repositorio, "n".repeat(101), "d".repeat(301), administrador))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO));
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_DEMASIADO_LARGA));
                });
    }
}
