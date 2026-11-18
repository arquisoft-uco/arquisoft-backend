package com.arquisoft.biblioteca.application.autor.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAutorCommandTest {

    @Test
    void debeCrearCommandConUuidConvertido_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var command = RemoverAutorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverAutorCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(BibliotecaFields.Autor.ID,
                                BibliotecaFields.Autor.IDENTIFICADOR, BibliotecaFields.Autor.NOMBRE,
                                BibliotecaFields.Autor.EMAIL, BibliotecaFields.Autor.OCURRIDO_EN));
    }
}
