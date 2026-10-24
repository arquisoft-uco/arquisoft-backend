package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioNombreRepositorioDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioNombreRepositorioDomain.crear(repositorio, "  Repositorio nuevo  ");

        // Assert
        assertThat(cambio.getRepositorio()).isEqualTo(repositorio);
        assertThat(cambio.getNombreRepositorio()).isEqualTo("Repositorio nuevo");
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> CambioNombreRepositorioDomain.crear(null, "   "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo())
                                .isEqualTo(ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_REQUERIDO);
                    });
                });
    }
}
