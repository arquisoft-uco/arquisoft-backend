package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgaPaquetesProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoElProyectoEsValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var purga = PurgaPaquetesProyectoGradoDomain.crear(proyecto);

        // Assert
        assertThat(purga.getProyectoGrado()).isEqualTo(proyecto);
    }

    @Test
    void debeFallar_cuandoElProyectoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> PurgaPaquetesProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactly(ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO));
    }
}
