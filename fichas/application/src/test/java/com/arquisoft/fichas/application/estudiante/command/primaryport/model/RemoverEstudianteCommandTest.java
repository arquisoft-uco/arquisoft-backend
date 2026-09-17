package com.arquisoft.fichas.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = RemoverEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElPayloadEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverEstudianteCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(FichasFields.Estudiante.ID, FichasFields.Estudiante.IDENTIFICADOR,
                                FichasFields.Estudiante.NOMBRE, FichasFields.Estudiante.EMAIL,
                                FichasFields.Estudiante.OCURRIDO_EN));
    }
}
