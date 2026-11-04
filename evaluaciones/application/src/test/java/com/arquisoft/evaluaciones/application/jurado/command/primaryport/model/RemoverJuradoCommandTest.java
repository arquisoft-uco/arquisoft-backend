package com.arquisoft.evaluaciones.application.jurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverJuradoCommandTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-12T10:00:00Z");

        // Act
        var command = RemoverJuradoCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoLosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverJuradoCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(EvaluacionesFields.Jurado.ID,
                                EvaluacionesFields.Jurado.IDENTIFICADOR, EvaluacionesFields.Jurado.NOMBRE,
                                EvaluacionesFields.Jurado.EMAIL, EvaluacionesFields.Jurado.OCURRIDO_EN));
    }
}
