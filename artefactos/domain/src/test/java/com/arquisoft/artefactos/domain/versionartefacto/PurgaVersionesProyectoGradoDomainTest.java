package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgaVersionesProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoProyectoValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var purga = PurgaVersionesProyectoGradoDomain.crear(proyecto);

        // Assert
        assertThat(purga.getProyectoGrado()).isEqualTo(proyecto);
    }

    @Test
    void debeFallar_cuandoProyectoNulo() {
        // Act & Assert
        assertThatThrownBy(() -> PurgaVersionesProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.ProyectoGrado.ID_REQUERIDO)));
    }
}
