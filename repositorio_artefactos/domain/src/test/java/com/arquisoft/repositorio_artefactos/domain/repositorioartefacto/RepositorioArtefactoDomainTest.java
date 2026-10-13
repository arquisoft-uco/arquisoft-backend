package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositorioArtefactoDomainTest {

    @Test
    void debeCrear_conIdGeneradoYTextosRecortados() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var repositorio = RepositorioArtefactoDomain.crear("  Repo Uno  ", "  Descripcion  ", administrador);

        // Assert
        assertThat(repositorio.getId()).isNotNull();
        assertThat(repositorio.getNombreRepositorio()).isEqualTo("Repo Uno");
        assertThat(repositorio.getDescripcion()).isEqualTo("Descripcion");
        assertThat(repositorio.getAdministrador()).isEqualTo(administrador);
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreVacioDescripcionLargaYAdministradorNulo() {
        // Arrange
        var descripcionLarga = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> RepositorioArtefactoDomain.crear("   ", descripcionLarga, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
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
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO);
                    });
                });
    }

    @Test
    void debeAceptarLosLimites_cuandoNombreEs100YDescripcionEs300() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> RepositorioArtefactoDomain.crear("n".repeat(100), "d".repeat(300), administrador))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarNombreDemasiadoLargo_cuandoNombreEs101() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RepositorioArtefactoDomain.crear("n".repeat(101), "Descripcion", administrador))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO)));
    }

    @Test
    void debeRechazarDescripcionRequerida_cuandoDescripcionEstaEnBlanco() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RepositorioArtefactoDomain.crear("Repo", " ", administrador))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> assertThat(error.codigoError())
                                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_REQUERIDA)));
    }
}
