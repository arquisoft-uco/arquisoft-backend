package com.arquisoft.fichas.application.asesorficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorFichaCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var command = RemoverAsesorFichaCommand.crear(
                id.toString(), "1036950123", "Laura Gomez", "laura@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("1036950123");
        assertThat(command.nombre()).isEqualTo("Laura Gomez");
        assertThat(command.email()).isEqualTo("laura@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElPayloadEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverAsesorFichaCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(FichasFields.AsesorFicha.ID, FichasFields.AsesorFicha.IDENTIFICADOR,
                                FichasFields.AsesorFicha.NOMBRE, FichasFields.AsesorFicha.EMAIL,
                                FichasFields.AsesorFicha.OCURRIDO_EN));
    }
}
